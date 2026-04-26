package com.zzyl.utils;

import com.zzyl.common.utils.PDFUtil;
import org.junit.jupiter.api.Test;

import java.io.*;

/**
 * TODO
 *
 * @Author: Zhy
 * @Date: 2025-02-28 11:50
 * @Version: 1.0
 */
public class PDFUtilTest {

	@Test
	public void testPdfToString() throws Exception {

		FileInputStream fileInputStream = new FileInputStream(
			"D:\\JAVAdevelop\\hmClass\\课程\\中州养老\\素材\\体检报告样例\\体检报告-刘爱国-男-69岁.pdf");

		String result = PDFUtil.pdfToString(fileInputStream);
		System.out.println(result);
	}

}
