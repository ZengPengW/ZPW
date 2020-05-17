import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class Test2 {
	
	
	/*
	 * 自定义输出流对象{
	 * 输出流
	 * 原地址
	 * 当前地址
	 * 是否是压缩包
	 * 
	 * }
	 * 
	 * 
	 * 1.把sql文件直接写入list 每个sql文件对应一个写出流 都是指向 /success/时间戳/目录下对应位置
	 * 1.如果是文件夹将整个文件夹copy到/success/时间戳/目录下对应位置  同时递归获取sql文件到list中 写出流为 /success/时间戳/目录下对应位置
	 * 2.如果是zip就把zip文件解压到临时文件夹内（D:\\zp\\zip2\\unzip\\+NewcurrentTimeMillis） 遍历 如果是sql就写入list 
	 *  创建输出流对象并存入ziplist中备用 输出流： D:\\zp\\zip2\\unziptemp\\+NewcurrentTimeMillis\\原名字.sql，原地址：压缩包内地址  是否是压缩包：true
	 * 3. 遍历全局map后 再遍历ziplist 把 通过参数当前地址获取文件 写入到原地址
	 * 4.再次遍历zipList通过原地址 把D:\\zp\\zip2\\unzip\\+NewcurrentTimeMillis 下文件夹压缩到第一步里 /success/时间戳/目录下对应位置
	 * 5.遍历/success/时间戳/ 目录 返回一个 list<file> 列表
	 * 
	 */
	
	public static void main(String[] args) throws ParseException, IOException {
		long currentTimeMillis = System.currentTimeMillis();
		unZipFiles(new File("D:/zp/zip/myzi.zip"), "D:\\zp\\zip2\\unzip\\"+currentTimeMillis);
		compress("D:\\zp\\zip2\\unzip\\"+currentTimeMillis,"D:\\zp\\zip2\\yasuo\\"+currentTimeMillis);
		System.out.println(getFileName(new File("D:\\zp\\zip2\\unzip\\"), true));
	}
	
	
	/**
	 * 获取文件名
	 * @param file 
	 * @param isType 是否带上文件类型格式 .txt 这些
	 * @return
	 * @throws IOException 
	 */
	public static String getFileName(File file,boolean isType) throws IOException {
		  String filecanonicalPath = file.getCanonicalPath();
		  filecanonicalPath=filecanonicalPath.replace(File.separator, "/");
		  String fileName=null;
		  if (!file.isDirectory()) {
			  fileName=filecanonicalPath.substring(filecanonicalPath.lastIndexOf("/")+1);
		  }else {
			  int lastIndexOf = filecanonicalPath.lastIndexOf("/");
			  if (lastIndexOf==filecanonicalPath.length()-1) {
				fileName=filecanonicalPath.substring(filecanonicalPath.lastIndexOf( "/",lastIndexOf-1));
			  }else {
				fileName=filecanonicalPath.substring(lastIndexOf+1);
			  }
			  
		  }
		  
		  if (!isType&&!file.isDirectory()) {
			fileName=fileName.substring(0,fileName.lastIndexOf("."));
		  }
		  return fileName;
		  
	}
	
	/**
	 *  递归解压文件到指定目录
	 * @param zipFile 要解压的压缩文件
	 * @param descDir 解压到指定目录
	 * @throws IOException
	 */
	public static void unZipFiles(File zipFile,String descDir)throws IOException
	  {
		  
		
		  if (null!=descDir) {
			  descDir=descDir.replace(File.separator, "/");
			  //获取zip文件名
			  String zipFileName = getFileName(zipFile,false);
			  
			  
			  if (!descDir.endsWith("/")) {
				  descDir+="/"+zipFileName+"/";
			  }else {
				  descDir=zipFileName+"/";
			  }
		  	}
		  if (descDir==null) {
			descDir=zipFile.getCanonicalPath();
			
			descDir=descDir.replace(File.separator, "/");
			String filename=descDir.substring(descDir.lastIndexOf("/")+1).replace(".", "");
			
			//System.out.println(descDir);
			descDir=descDir.substring(0,descDir.lastIndexOf("/")+1)+filename+"/";
			
		  }
		  
		 
	    File pathFile = new File(descDir);
	    if(!pathFile.exists())
	    {
	      pathFile.mkdirs();
	    }
	    //解决zip文件中有中文目录或者中文文件
	    ZipFile zip = new ZipFile(zipFile, Charset.forName("GBK"));
	    for(Enumeration entries = zip.entries(); entries.hasMoreElements();)
	    {
	      ZipEntry entry = (ZipEntry)entries.nextElement();
	      String zipEntryName = entry.getName();
	      InputStream in = zip.getInputStream(entry);
	      String outPath = (descDir+zipEntryName).replaceAll("\\*", "/");;
	      //判断路径是否存在,不存在则创建文件路径
	      File file=null ;
	    
		  file = new File(outPath.substring(0, outPath.lastIndexOf('/')));

		
	      
	      if(!file.exists())
	      {
	        file.mkdirs();
	      }
	      //判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
	      if(new File(outPath).isDirectory())
	      {
	        continue;
	      }
	      //输出文件路径信息
	      System.out.println(outPath);
	      OutputStream out = new FileOutputStream(outPath);
	      byte[] buf1 = new byte[1024];
	      int len;
	      while((len=in.read(buf1))>0)
	      {
	        out.write(buf1,0,len);
	      }
	      in.close();
	      out.flush();
	      out.close();
	      
	      //递归解压压缩包内的压缩包
	      if (outPath.endsWith(".zip")) {
	    //	  System.out.println("s+ "+outPath);
	    	  File nestZipFile = new File(outPath);
	    	  unZipFiles(nestZipFile, null);
	    	  nestZipFile.delete();
	    	  
			
		}
	      
	    }
	    System.out.println("******************解压完毕********************");
	  }

	  /**
	   * 压缩文件目录下以文件夹为单位生成各个压缩文件
	   * @param srcFilePath 压缩源文件夹
	   * @param destFilePath 压缩目的路径
	 * @throws IOException 
	   */
	  public static void compress(String srcFilePath, String destFilePath) throws IOException{
		// unZipFiles(new File("D:/zp/zip/myzi.zip"), "D:\\zp\\zip2\\unzip");
			//	compress("D:\\zp\\zip2\\unzip","D:\\zp\\zip2\\yasuo\\");
		  File srcFile=new File(srcFilePath);
		  File [] fileList = srcFile.listFiles();
		  for (int i = 0; i <fileList.length;  i++) {
			if (fileList[i].isDirectory()) {
				File zipFile = new File(destFilePath);
		        String dir=zipFile.getCanonicalPath().replace(File.separator, "/");
		        if (!dir.endsWith("/")) {
					dir+="/";
				}
		        String fileName = getFileName(fileList[i], false);
				compress(fileList[i], dir+fileName+".zip");
			}
			  
		  }
		
	  }

	  /**s
	     * 压缩文件
	     * @param srcFilePath 压缩源
	     * @param destFilePath 压缩目的路径
	     * @throws IOException 
	     */
	  public static void compress(File srcFilePath, String destFilePath) throws IOException {
	        //
	 
	        
	        if (!srcFilePath.exists()) {
	            throw new RuntimeException(srcFilePath + "不存在");
	        }
	        File zipFile = new File(destFilePath);
	        String dir=zipFile.getCanonicalPath().replace(File.separator, "/");
	        
	        File file = new File(dir.substring(0,dir.lastIndexOf("/")));
	        if (!file.exists()) {	        		        	
	        	file.mkdirs();
	        }
	      
	        try {
	 
	            FileOutputStream fos = new FileOutputStream(zipFile);
	            ZipOutputStream zos = new ZipOutputStream(fos);
	            String baseDir = "";
	            compressbyType(srcFilePath, zos, baseDir);
	            zos.close();
	 
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    /**
	     * 按照原路径的类型就行压缩。文件路径直接把文件压缩，
	     * @param src
	     * @param zos
	     * @param baseDir
	     */
	     private static void compressbyType(File src, ZipOutputStream zos,String baseDir) {
	 
	            if (!src.exists())
	                return;
	            System.out.println("压缩路径" + baseDir + src.getName());
	            //判断文件是否是文件，如果是文件调用compressFile方法,如果是路径，则调用compressDir方法；
	            if (src.isFile()) {
	                //src是文件，调用此方法
	                compressFile(src, zos, baseDir);
	                 
	            } else if (src.isDirectory()) {
	                //src是文件夹，调用此方法
	                compressDir(src, zos, baseDir);
	 
	            }
	 
	        }
	      
	        /**
	         * 压缩文件
	        */
	        private static void compressFile(File file, ZipOutputStream zos,String baseDir) {
	            if (!file.exists())
	                return;
	            try {
	            	String canonicalPath = file.getCanonicalPath();
	            	String fileType=canonicalPath.substring(canonicalPath.lastIndexOf("."));
	            	if (fileType.contains("zip")) {
						System.out.println();
					}
	            	if (fileType.equals(".zip")) {
						return;
					}
	            	
	                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
	                ZipEntry entry = new ZipEntry(baseDir + file.getName());
	                zos.putNextEntry(entry);
	                int count;
	                byte[] buf = new byte[1024];
	                while ((count = bis.read(buf)) != -1) {
	                    zos.write(buf, 0, count);
	                }
	                bis.close();
	 
	            } catch (Exception e) {
	              // TODO: handle exception
	 
	            }
	        }
	         
	        /**
	         * 压缩文件夹
	         */
	        private static void compressDir(File dir, ZipOutputStream zos,String baseDir) {
	            if (!dir.exists())
	                return;
	            File[] files = dir.listFiles();
	            if(files.length == 0){
	                try {
	                    zos.putNextEntry(new ZipEntry(baseDir + dir.getName()+File.separator));
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	            for (File file : files) {
	                compressbyType(file, zos, baseDir + dir.getName() + File.separator);
	            }
	    }
	}

