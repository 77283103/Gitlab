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
package org.springblade.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.springblade.core.mp.base.BaseService;
import org.springblade.system.entity.Post;
import org.springblade.system.vo.PostVO;

import java.util.List;

/**
 * 岗位表 服务类
 *
 * @author Chill
 */
public interface IPostService extends BaseService<Post> {




	/**
	 * 分页查询
	 * @param page
	 * @param post
	 * @return
	 */
	IPage<Post> pageList(IPage<Post> page, Post post);

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param post
	 * @return
	 */
	IPage<PostVO> selectPostPage(IPage<PostVO> page, PostVO post);

	/**
	 * 获取岗位ID
	 *
	 * @param tenantId
	 * @param postNames
	 * @return
	 */
	String getPostIds(String tenantId, String postNames);

	/**
	 * 获取岗位名
	 *
	 * @param postIds
	 * @return
	 */
	List<String> getPostNames(String postIds);

	/**
	 * 查询是否有重复的岗位id
	 *
	 * @param postCode 岗位id
	 * @return
	 */
	boolean getByCode(String postCode);

	/**
	 * 根据lunid获取Id
	 *
	 * @param associationId 接口唯一标识
	 * @return 岗位id
	 */
	Long getPostIdByAssociationId(String associationId);


	/**
	 * 批量保存岗位
	 */
	boolean saveBatchPost(List<Post> postList);

}
