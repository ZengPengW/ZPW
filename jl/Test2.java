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
	 * �Զ������������{
	 * �����
	 * ԭ��ַ
	 * ��ǰ��ַ
	 * �Ƿ���ѹ����
	 * 
	 * }
	 * 
	 * 
	 * 1.��sql�ļ�ֱ��д��list ÿ��sql�ļ���Ӧһ��д���� ����ָ�� /success/ʱ���/Ŀ¼�¶�Ӧλ��
	 * 1.������ļ��н������ļ���copy��/success/ʱ���/Ŀ¼�¶�Ӧλ��  ͬʱ�ݹ��ȡsql�ļ���list�� д����Ϊ /success/ʱ���/Ŀ¼�¶�Ӧλ��
	 * 2.�����zip�Ͱ�zip�ļ���ѹ����ʱ�ļ����ڣ�D:\\zp\\zip2\\unzip\\+NewcurrentTimeMillis�� ���� �����sql��д��list 
	 *  ������������󲢴���ziplist�б��� ������� D:\\zp\\zip2\\unziptemp\\+NewcurrentTimeMillis\\ԭ����.sql��ԭ��ַ��ѹ�����ڵ�ַ  �Ƿ���ѹ������true
	 * 3. ����ȫ��map�� �ٱ���ziplist �� ͨ��������ǰ��ַ��ȡ�ļ� д�뵽ԭ��ַ
	 * 4.�ٴα���zipListͨ��ԭ��ַ ��D:\\zp\\zip2\\unzip\\+NewcurrentTimeMillis ���ļ���ѹ������һ���� /success/ʱ���/Ŀ¼�¶�Ӧλ��
	 * 5.����/success/ʱ���/ Ŀ¼ ����һ�� list<file> �б�
	 * 
	 */
	
	public static void main(String[] args) throws ParseException, IOException {
		long currentTimeMillis = System.currentTimeMillis();
		unZipFiles(new File("D:/zp/zip/myzi.zip"), "D:\\zp\\zip2\\unzip\\"+currentTimeMillis);
		compress("D:\\zp\\zip2\\unzip\\"+currentTimeMillis,"D:\\zp\\zip2\\yasuo\\"+currentTimeMillis);
		System.out.println(getFileName(new File("D:\\zp\\zip2\\unzip\\"), true));
	}
	
	
	/**
	 * ��ȡ�ļ���
	 * @param file 
	 * @param isType �Ƿ�����ļ����͸�ʽ .txt ��Щ
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
	 *  �ݹ��ѹ�ļ���ָ��Ŀ¼
	 * @param zipFile Ҫ��ѹ��ѹ���ļ�
	 * @param descDir ��ѹ��ָ��Ŀ¼
	 * @throws IOException
	 */
	public static void unZipFiles(File zipFile,String descDir)throws IOException
	  {
		  
		
		  if (null!=descDir) {
			  descDir=descDir.replace(File.separator, "/");
			  //��ȡzip�ļ���
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
	    //���zip�ļ���������Ŀ¼���������ļ�
	    ZipFile zip = new ZipFile(zipFile, Charset.forName("GBK"));
	    for(Enumeration entries = zip.entries(); entries.hasMoreElements();)
	    {
	      ZipEntry entry = (ZipEntry)entries.nextElement();
	      String zipEntryName = entry.getName();
	      InputStream in = zip.getInputStream(entry);
	      String outPath = (descDir+zipEntryName).replaceAll("\\*", "/");;
	      //�ж�·���Ƿ����,�������򴴽��ļ�·��
	      File file=null ;
	    
		  file = new File(outPath.substring(0, outPath.lastIndexOf('/')));

		
	      
	      if(!file.exists())
	      {
	        file.mkdirs();
	      }
	      //�ж��ļ�ȫ·���Ƿ�Ϊ�ļ���,����������Ѿ��ϴ�,����Ҫ��ѹ
	      if(new File(outPath).isDirectory())
	      {
	        continue;
	      }
	      //����ļ�·����Ϣ
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
	      
	      //�ݹ��ѹѹ�����ڵ�ѹ����
	      if (outPath.endsWith(".zip")) {
	    //	  System.out.println("s+ "+outPath);
	    	  File nestZipFile = new File(outPath);
	    	  unZipFiles(nestZipFile, null);
	    	  nestZipFile.delete();
	    	  
			
		}
	      
	    }
	    System.out.println("******************��ѹ���********************");
	  }

	  /**
	   * ѹ���ļ�Ŀ¼�����ļ���Ϊ��λ���ɸ���ѹ���ļ�
	   * @param srcFilePath ѹ��Դ�ļ���
	   * @param destFilePath ѹ��Ŀ��·��
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
	     * ѹ���ļ�
	     * @param srcFilePath ѹ��Դ
	     * @param destFilePath ѹ��Ŀ��·��
	     * @throws IOException 
	     */
	  public static void compress(File srcFilePath, String destFilePath) throws IOException {
	        //
	 
	        
	        if (!srcFilePath.exists()) {
	            throw new RuntimeException(srcFilePath + "������");
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
	     * ����ԭ·�������;���ѹ�����ļ�·��ֱ�Ӱ��ļ�ѹ����
	     * @param src
	     * @param zos
	     * @param baseDir
	     */
	     private static void compressbyType(File src, ZipOutputStream zos,String baseDir) {
	 
	            if (!src.exists())
	                return;
	            System.out.println("ѹ��·��" + baseDir + src.getName());
	            //�ж��ļ��Ƿ����ļ���������ļ�����compressFile����,�����·���������compressDir������
	            if (src.isFile()) {
	                //src���ļ������ô˷���
	                compressFile(src, zos, baseDir);
	                 
	            } else if (src.isDirectory()) {
	                //src���ļ��У����ô˷���
	                compressDir(src, zos, baseDir);
	 
	            }
	 
	        }
	      
	        /**
	         * ѹ���ļ�
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
	         * ѹ���ļ���
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

