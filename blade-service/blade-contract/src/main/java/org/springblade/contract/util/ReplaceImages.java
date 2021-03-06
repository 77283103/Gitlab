package org.springblade.contract.util;

import com.spire.doc.Document;
import com.spire.doc.DocumentObject;
import com.spire.doc.FileFormat;
import com.spire.doc.Section;
import com.spire.doc.documents.Paragraph;
import com.spire.doc.fields.DocPicture;
import lombok.AllArgsConstructor;
import org.springblade.resource.feign.IFileClient;
import org.springframework.stereotype.Component;
import com.spire.pdf.*;
import com.spire.pdf.general.find.PdfTextFind;
import com.spire.pdf.general.find.PdfTextFindCollection;
import com.spire.pdf.graphics.PdfBrushes;
import com.spire.pdf.graphics.PdfRGBColor;
import com.spire.pdf.graphics.PdfSolidBrush;
import com.spire.pdf.graphics.PdfTrueTypeFont;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * @author xhbbo
 * Java 使用新图片替换 Word 文档中的现有图片
 */
@AllArgsConstructor
@Component
public class ReplaceImages {
    private IFileClient fileClient;
    private static ReplaceImages replaceImages;
	//初始化
	@PostConstruct
    public void init(){
		replaceImages=this;
	}

	/**
	 *
	 * @param filepaths
	 */
	public static void replaceImage(List<String> filepaths,List<String > imagepths) {
		//加载Word文档
		Document doc = new Document();
		doc.loadFromFile(filepaths.get(0));
		//获取文档中的第一个节
		Section section = doc.getSections().get(0);
		//遍历该节中的所有段落
		for (Paragraph para : (Iterable<Paragraph>) section.getParagraphs()) {
			//遍历每个段落中的子元素
			for (DocumentObject obj : (Iterable<DocumentObject>) para.getChildObjects()) {
				//使用新图片替换文档中的现有图片
				if (obj instanceof DocPicture) {
					DocPicture pic = (DocPicture) obj;
					//将上传的图片替换掉文本原有的图片
						pic.loadImage(imagepths.get(0));
				}
			}
		}
		//保存结果文档
		doc.saveToFile(filepaths.get(1), FileFormat.Docx_2013);
	}


	public static void FindAndReplaceText (String docPath, String pdfPath) {
		//加载示例PDF文档
		PdfDocument pdf = new PdfDocument();
		pdf.loadFromFile(docPath);
		//遍历文档每一页
		for (int i = 0; i < pdf.getPages().getCount(); i++) {
			//获取所有页面
			PdfPageBase page = pdf.getPages().get(i);
			//查找指定文本
			PdfTextFindCollection textFindCollection;
			textFindCollection = page.findText("Evaluation Warning : The document was created with Spire.PDF for Java.",false);
			//创建画刷、字体
			PdfSolidBrush brush1 = new PdfSolidBrush(new PdfRGBColor(Color.white));
			PdfTrueTypeFont font1= new PdfTrueTypeFont(new Font("宋体",Font.PLAIN,2),true);
			//用新的文本字符替换原有文本
			Rectangle2D rec;
			for(PdfTextFind find: textFindCollection.getFinds())
			{
				rec = find.getBounds();
				page.getCanvas().drawRectangle(PdfBrushes.getWhite(), rec);
				page.getCanvas().drawString("Coffee", font1, brush1, rec);
			}
		}
		//保存文档
		pdf.saveToFile(pdfPath);
		pdf.close();
	}
}
