package com.zzyl.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.io.InputStream;

/**
 * PDF工具类
 *
 * @Author: Zhy
 * @Date: 2025-02-28 12:04
 * @Version: 1.0
 */
@Slf4j
public class PDFUtil {

	public static String pdfToString(InputStream inputStream) {

		PDDocument document = null;

		try {
			// 加载PDF文档
			document = PDDocument.load(inputStream);

			// 创建一个PDFTextStripper实例来提取文本
			PDFTextStripper pdfStripper = new PDFTextStripper();

			// 从PDF文档中提取文本
			return pdfStripper.getText(document);

		} catch (IOException e) {
			log.error("PDF转字符串异常：", e);
		} finally {
			// 关闭PDF文档
			if (document != null) {
				try {
					document.close();
					inputStream.close();
				} catch (IOException e) {
					log.error("关闭PDF文档异常：", e);
				}
			}
		}
		return null;
	}
}