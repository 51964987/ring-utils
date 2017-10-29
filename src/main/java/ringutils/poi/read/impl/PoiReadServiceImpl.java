package ringutils.poi.read.impl;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import ringutils.poi.read.PoiReadService;
import ringutils.poi.read.ReadSheetContentsHandler;

public class PoiReadServiceImpl implements PoiReadService {
	
	private Logger log = LoggerFactory.getLogger(PoiReadServiceImpl.class);
	
	private List<String> listFilePath;
	
	@Override
	public void read(String path, String sheetnamePattern, ReadSheetContentsHandler sheetContentsHandler) throws Exception {
		OPCPackage xlsxPackage = null;
		long start = System.currentTimeMillis();
		try {
			xlsxPackage = OPCPackage.open(path,PackageAccess.READ);
			ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(xlsxPackage);
			XSSFReader xssfReader = new XSSFReader(xlsxPackage);
			StylesTable styles = xssfReader.getStylesTable();
			XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator)xssfReader.getSheetsData();
			boolean issheetname = false;
			while(iter.hasNext()){
				InputStream stream = iter.next();
				if(Pattern.matches(sheetnamePattern, iter.getSheetName())){		
					issheetname = true;
					InputSource sheetSource = new InputSource(stream);
					
					SAXParserFactory saxFactory = SAXParserFactory.newInstance();
					SAXParser saxParser = saxFactory.newSAXParser();
					XMLReader sheetparser = saxParser.getXMLReader();
					
					sheetContentsHandler.setSheetname(iter.getSheetName());
					ContentHandler handler = new XSSFSheetXMLHandler(styles, strings,sheetContentsHandler, false);
					sheetparser.setContentHandler(handler);
					sheetparser.parse(sheetSource);
				}
				stream.close();
			}
			if(!issheetname){
				throw new Exception("导入文件有误，请重新导入");
			}
			if(sheetContentsHandler.getDataCount()<=0){
				throw new Exception("导入（"+sheetnamePattern+"）数据为空，请重新导入");
			}
		} catch (Exception e) {
			throw e;
		}finally{
			if(xlsxPackage!=null){
				xlsxPackage.close();
			}
			log.info("耗时"+(float)(System.currentTimeMillis()-start)/1000+"秒读取文件："+path);
		}
	}

	@Override
	public void deleteListFilePath(List<String> listFilePath) throws Exception {
		if(listFilePath!=null&&listFilePath.size()>0){
			for(String path : listFilePath){
				new File(path).delete();
			}
		}
	}

	@Override
	public List<String> listFilePath(HttpServletRequest request) throws Exception {
		List<String> list = new ArrayList<>();
		try {
			DiskFileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload fu = new ServletFileUpload(factory);
			fu.setHeaderEncoding("utf-8");
			List<?> items = fu.parseRequest(request);
			Iterator<?> it = items.iterator();
			String tmpPath = System.getProperty("java.io.tmpdir");
			while(it.hasNext()){
				FileItem item = (FileItem) it.next();
				if(!item.isFormField()){
					if(item.getName() == null || "".equals(item.getName().trim())){
						continue;
					}
					if(!Pattern.matches("(?!).*\\.(?:xls|xlsx)", item.getName())){
						throw new FileUploadException("仅支持xls或xlsx文件");
					}
					String path = tmpPath+""+new File(item.getName()).getName();
					long start = System.currentTimeMillis();
					item.write(new File(path));
					log.info("耗时"+(float)(System.currentTimeMillis()-start)/1000+"秒生成文件");
					list.add(path);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return list;
	}

	public List<String> getListFilePath() {
		return listFilePath;
	}
	public void setListFilePath(List<String> listFilePath) {
		this.listFilePath = listFilePath;
	}


}
