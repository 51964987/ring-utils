package ringutils.file;

import java.io.File;
import java.util.List;

public class FileDeleteUtil {
	
	/**
	 * 递归删除目录下的所有文件及子目录下所有文件
	 * @param dir 
	 * @author ring
	 * @date 2017年3月29日 下午9:43:49
	 * @version V1.0
	 */
	public static void deleteByDir(String dir){
		File dirFile = new File(dir);
		File[] list = dirFile.listFiles();
		if(list!=null&&list.length>0){
			for(File file : list){
				if(file.isDirectory()){
					deleteByDir(file.getAbsolutePath());
					file.delete();
				}else if(file.isFile()){
					deleteByFilename(file.getAbsolutePath());
				}
			}
			dirFile.delete();
		}
	}
	
	/**
	 * 根据文件名删除
	 * @param filename 
	 * @author ring
	 * @date 2017年3月29日 下午3:25:08
	 * @version V1.0
	 */
	public static void deleteByFilename(String filename){
		new File(filename).delete();
	}

	/**
	 * 批量删除
	 * @param uploadDir
	 * @param filenames 
	 * @author ring
	 * @date 2017年3月29日 下午9:44:00
	 * @version V1.0
	 */
	public static void deleteByDir(List<String> filenames) {
		for(String filename : filenames){
			deleteByFilename(filename);
		}
	}
}
