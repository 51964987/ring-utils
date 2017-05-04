package ringutils.image;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

/**
 * 读取图片信息
 *  
 * @author ring
 * @date 2017年4月1日 下午7:01:07
 * @version V1.0
 */
@SuppressWarnings("unchecked")
public class ImageReaderUtil {
	
	private static Logger log = LoggerFactory.getLogger(ImageReaderUtil.class);
	
	/**
	 * 获取图片信息
	 * @param is	文件输入流
	 * @return
	 * @throws Exception 
	 * @author ring
	 * @date 2017年4月1日 下午9:18:00
	 * @version V1.0
	 */
	public static JSONObject readImageMetadata(InputStream is) throws Exception{
		return readImageMetadata(ImageMetadataReader.readMetadata(new BufferedInputStream(is)));
	}
	
	/**
	 * 获取图片信息
	 * @param imgFile	文件对象
	 * @return
	 * @throws Exception 
	 * @author ring
	 * @date 2017年4月1日 下午9:17:37
	 * @version V1.0
	 */
	public static JSONObject readImageMetadata(File imgFile) throws Exception {
		return readImageMetadata(ImageMetadataReader.readMetadata(imgFile));
	}
	
	/**
	 * 获取图片信息
	 * @param metadata	核心对象操作对象
	 * @return
	 * @throws Exception 
	 * @author ring
	 * @date 2017年4月1日 下午9:17:10
	 * @version V1.0
	 */
	private static JSONObject readImageMetadata(Metadata metadata) throws Exception {
		JSONObject infos = null;
		try {
			// 获取所有不同类型的Directory，如ExifSubIFDDirectory, ExifInteropDirectory,
			// ExifThumbnailDirectory等，这些类均为ExifDirectoryBase extends
			// Directory子类
			// 分别遍历每一个Directory，根据Directory的Tags就可以读取到相应的信息
			Iterator<Directory> iter = metadata.getDirectoryIterator();
			infos = new JSONObject();
			while (iter.hasNext()) {
				Directory dr = iter.next();
				Iterator<Tag> tags = dr.getTagIterator();
				while (tags.hasNext()) {
					Tag tag = tags.next();
					infos.put(tag.getTagName() , tag.getDescription());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(),e);
			throw new Exception(e.getMessage(),e);
		}
		return infos;
	}

}
