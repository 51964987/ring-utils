package ringutils.packageutil;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ringutils.date.DateUtil;

public class PackageUtil {
	private Logger log = LoggerFactory.getLogger(getClass());
	private File sourcePath;
	private File targetPath;
	private Date lastDate;
	private int count;
	private List<String> excludeDirectory = new ArrayList<>();
	
	public PackageUtil(String sourcePath, String targetPath, String lastDate) throws ParseException {
		super();
		this.sourcePath = new File(sourcePath);
		this.targetPath = new File(new File(targetPath).getAbsolutePath()+File.separator+this.sourcePath.getName());
		this.lastDate = DateUtil.parseDate(lastDate);
	}
	
	public void packagingIncludeFileType(String includeFileType) throws IOException{
		packaging(sourcePath, includeFileType, true);
	}
	
	public void packagingExcludeFileType(String excludeFileType) throws IOException{
		packaging(sourcePath, excludeFileType, false);
	}

	private void packaging(File sourcePath,String fileType,boolean include) throws IOException{
		File[] fileList = sourcePath.listFiles();
		for(File tmpFile : fileList){
			if(tmpFile.isFile()){
				Date fileDate = new Date(tmpFile.lastModified());
				if(fileDate.getTime() > lastDate.getTime()){
					boolean fileTypeBoolean = Pattern.matches("(?i).*\\.(?:"+fileType+")", tmpFile.getAbsolutePath());
					if(include == false){
						fileTypeBoolean = !fileTypeBoolean;
					}
					if(fileTypeBoolean){
						copyFile(tmpFile,fileType);
					}
				}
			}else if(tmpFile.isDirectory() && !excludeDirectory.contains(tmpFile.getName())){
				packaging(sourcePath, fileType, include);
			}
		}
	}
	
	private void copyFile(File srcFile, String fileType) throws IOException {
		String targetPath = srcFile.getAbsolutePath().replace(this.sourcePath.getAbsolutePath(), this.targetPath.getAbsolutePath());
		log.info("packing："+targetPath);
		File targetFile = new File(targetPath);
		targetFile.getParentFile().mkdirs();
		FileUtils.copyFile(srcFile, targetFile);
	}

	public File getSourcePath() {
		return sourcePath;
	}
	public File getTargetPath() {
		return targetPath;
	}
	public Date getLastDate() {
		return lastDate;
	}
	public int getCount() {
		return count;
	}
	public List<String> getExcludeDirectory() {
		return excludeDirectory;
	}
	
}
