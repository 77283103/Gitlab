/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: Chill 庄骞 (smallchill@163.com)
 */
package org.springblade.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import org.springblade.common.constant.CommonConstant;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.secure.utils.SecureUtil;
import org.springblade.core.tool.constant.BladeConstant;
import org.springblade.core.tool.constant.RoleConstant;
import org.springblade.core.tool.node.ForestNodeMerger;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.system.dto.RoleDTO;
import org.springblade.system.entity.Role;
import org.springblade.system.entity.RoleMenu;
import org.springblade.system.entity.RoleScope;
import org.springblade.system.mapper.RoleMapper;
import org.springblade.system.service.IRoleMenuService;
import org.springblade.system.service.IRoleScopeService;
import org.springblade.system.service.IRoleService;
import org.springblade.system.vo.DictVO;
import org.springblade.system.vo.RoleVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static org.springblade.common.constant.CommonConstant.API_SCOPE_CATEGORY;
import static org.springblade.common.constant.CommonConstant.DATA_SCOPE_CATEGORY;

/**
 * 服务实现类
 *
 * @author Chill
 */
@Service
@Validated
@AllArgsConstructor
public class RoleServiceImpl extends BaseServiceImpl<RoleMapper, Role> implements IRoleService {

	private IRoleMenuService roleMenuService;
	private IRoleScopeService roleScopeService;

	@Override
	public IPage<RoleVO> selectRolePage(IPage<RoleVO> page, RoleVO role) {
		return page.setRecords(baseMapper.selectRolePage(page, role));
	}

	@Override
	public List<RoleVO> tree(String tenantId) {
		String userRole = SecureUtil.getUserRole();
		String excludeRole = null;
		if (!CollectionUtil.contains(Func.toStrArray(userRole), RoleConstant.ADMIN) && !CollectionUtil.contains(Func.toStrArray(userRole), RoleConstant.ADMINISTRATOR)) {
			excludeRole = RoleConstant.ADMINISTRATOR;
		}
		return ForestNodeMerger.merge(baseMapper.tree(tenantId, excludeRole));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean grant(@NotEmpty List<Long> roleIds, List<Long> menuIds, List<Long> dataScopeIds, List<Long> apiScopeIds) {
		// 删除角色配置的菜单集合
		roleMenuService.remove(Wrappers.<RoleMenu>update().lambda().in(RoleMenu::getRoleId, roleIds));
		// 组装配置
		List<RoleMenu> roleMenus = new ArrayList<>();
		roleIds.forEach(roleId -> menuIds.forEach(menuId -> {
			RoleMenu roleMenu = new RoleMenu();
			roleMenu.setRoleId(roleId);
			roleMenu.setMenuId(menuId);
			roleMenus.add(roleMenu);
		}));
		// 新增配置
		roleMenuService.saveBatch(roleMenus);

		// 删除角色配置的数据权限集合
		roleScopeService.remove(Wrappers.<RoleScope>update().lambda().eq(RoleScope::getScopeCategory, DATA_SCOPE_CATEGORY).in(RoleScope::getRoleId, roleIds));
		// 组装配置
		List<RoleScope> roleDataScopes = new ArrayList<>();
		roleIds.forEach(roleId -> dataScopeIds.forEach(scopeId -> {
			RoleScope roleScope = new RoleScope();
			roleScope.setScopeCategory(DATA_SCOPE_CATEGORY);
			roleScope.setRoleId(roleId);
			roleScope.setScopeId(scopeId);
			roleDataScopes.add(roleScope);
		}));
		// 新增配置
		roleScopeService.saveBatch(roleDataScopes);

		// 删除角色配置的接口权限集合
		roleScopeService.remove(Wrappers.<RoleScope>update().lambda().eq(RoleScope::getScopeCategory, API_SCOPE_CATEGORY).in(RoleScope::getRoleId, roleIds));
		// 组装配置
		List<RoleScope> roleApiScopes = new ArrayList<>();
		roleIds.forEach(roleId -> apiScopeIds.forEach(scopeId -> {
			RoleScope roleScope = new RoleScope();
			roleScope.setScopeCategory(API_SCOPE_CATEGORY);
			roleScope.setScopeId(scopeId);
			roleScope.setRoleId(roleId);
			roleApiScopes.add(roleScope);
		}));
		// 新增配置
		roleScopeService.saveBatch(roleApiScopes);

		return true;
	}

	@Override
	public String getRoleIds(String tenantId, String roleNames) {
		List<Role> roleList = baseMapper.selectList(Wrappers.<Role>query().lambda().eq(Role::getTenantId, tenantId).in(Role::getRoleName, Func.toStrList(roleNames)));
		if (roleList != null && roleList.size() > 0) {
			return roleList.stream().map(role -> Func.toStr(role.getId())).distinct().collect(Collectors.joining(","));
		}
		return null;
	}

	@Override
	public List<String> getRoleNames(String roleIds) {
		return baseMapper.getRoleNames(Func.toLongArray(roleIds));
	}

	@Override
	public List<String> getRoleAliases(String roleIds) {
		return baseMapper.getRoleAliases(Func.toLongArray(roleIds));
	}

	@Override
	public boolean submit(Role role) {
		if (!AuthUtil.isAdministrator()) {
			if (Func.toStr(role.getRoleAlias()).equals(RoleConstant.ADMINISTRATOR)) {
				throw new ServiceException("无权限创建超管角色！");
			}
		}
		if (Func.isEmpty(role.getId())) {
			role.setTenantId(AuthUtil.getTenantId());
		}
		if (Func.isEmpty(role.getParentId())) {
			role.setParentId(BladeConstant.TOP_PARENT_ID);
		}
		role.setIsDeleted(BladeConstant.DB_NOT_DELETED);
		return saveOrUpdate(role);
	}

	@Override
	public boolean removeByIdsAndSon(List<Long> idsList) {
		Set<Long> idsSet = Sets.newHashSet(idsList);
		idsSet = this.recursionSelect(idsSet);
		return super.removeByIds(idsSet);
	}

	/**
	 * 递归查询子级
	 * @param idsSet
	 * @return
	 */
	private Set<Long> recursionSelect(Set<Long> idsSet){
		List<Role> roles = baseMapper.selectRoleByParentId(idsSet);
		if (CollectionUtil.isNotEmpty(roles)){
			Set<Long> set = roles.stream().map(Role::getId).collect(Collectors.toSet());
			set = this.recursionSelect(set);
			idsSet.addAll(set);
		}
		return idsSet;
	}
	@Override
	public List<RoleVO> lazyList(Long parentId, Map<String, Object> param) {
		if (Func.isEmpty(Func.toStr(param.get(CommonConstant.PARENT_ID)))) {
			parentId = null;
		}
		return baseMapper.lazyList(parentId, param);
	}
}
