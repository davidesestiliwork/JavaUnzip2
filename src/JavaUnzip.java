/*
	JavaUnzip2 a simple program for unzipping a directory
	Copyright (C) 2023 Davide Sestili

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class JavaUnzip 
{
	protected static final int BUFFER_SIZE = 128 * 1024;
	
	public static void main(String[] args) 
	{
		if(args.length == 2)
		{
			try
			{
				File zipFile = new File(args[0]);
				File outputDir = new File(args[1]);
				
				if(!zipFile.getAbsolutePath().toLowerCase().endsWith(".zip"))
				{
					System.out.println("Is not a zip file");
					return;
				}
				
				if(!zipFile.exists())
				{
					System.out.println("zip file does not exist");
					return;
				}

				if(!zipFile.isFile())
				{
					System.out.println("Is not a file");
					return;
				}
				
				if(outputDir.exists())
				{
					System.out.println("Output dir already exist");
					return;
				}
				else
				{
					boolean created = outputDir.mkdir();
					if(!created)
					{
						throw new Exception("Error creating directory");
					}
				}
				
				new JavaUnzip().createFiles(zipFile, outputDir);
			}
			catch(Throwable t)
			{
				t.printStackTrace();
			}
		}
		else
		{
			System.out.println("Usage: param 1: zip file path, param 2: output directory path");
		}
	}
	
	private void createFiles(File zipFile, File outputDir) throws Throwable
	{
		byte buf[] = new byte[BUFFER_SIZE];
		
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		ZipInputStream input = null;
		
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		
		try
		{
			fis = new FileInputStream(zipFile);
			bis = new BufferedInputStream(fis);
			input = new ZipInputStream(bis);
			
			ZipEntry entry = null;
			while((entry = input.getNextEntry()) != null)
			{
				if(!entry.isDirectory())
				{
					try
					{
						createPath(entry.getName(), outputDir);
						File f = new File(outputDir.getAbsolutePath() + File.separator  + entry.getName());
						System.out.println("Writing file: " + f.getAbsolutePath());
						fos = new FileOutputStream(f);
						bos = new BufferedOutputStream(fos);
					
						int len;
						while((len = input.read(buf, 0, buf.length)) != -1)
						{
						   bos.write(buf, 0, len);
						}
					}
					finally
					{
						bos.close();
					}
				}
			}
		}
		finally
		{
			input.close();
		}
	}
	
	private void createPath(String fileName, File outputDir) throws Throwable
	{
		String[] dirs = null;
		try
		{
			int separators = 0;
			for(int i = 0; i < fileName.length(); i++)
			{
				if(fileName.charAt(i) == File.separator.charAt(0))
				{
					separators++;
				}
			}

			dirs = new String[separators];
			StringBuffer[] stringBuffer = new StringBuffer[separators];
			int j = 0;
			for(int i = 0; i < fileName.length(); i++)
			{
				if(fileName.charAt(i) == File.separator.charAt(0))
				{
					stringBuffer[j] = new StringBuffer();
				}
				else
				{
					for(; j < fileName.length() && i < fileName.length() ;)
					{
						if(fileName.charAt(i) == File.separator.charAt(0))
						{
							j++;
							i--;
							break;
						}
						stringBuffer[j].append(fileName.charAt(i));
						i++;
					}
				}
			}

			for(int i = 0; i < separators; i++)
			{
				dirs[i] = stringBuffer[i].toString();
			}
			//dirs = fileName.split(File.separator);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		if(dirs.length >= 2)
		{
			StringBuffer sb = new StringBuffer();
			
			for(int i = 0; i < (dirs.length - 1); i++)
			{
				if(i == 0 && dirs[i].equals(""))
				{
					continue;
				}
				
				sb.append(dirs[i]);
				if(i < (dirs.length - 2))
				{
					sb.append(File.separator);
				}
			}

			File dir = new File(outputDir.getAbsolutePath() + File.separator + sb.toString());
			if(!dir.exists())
			{
				boolean created = dir.mkdirs();
				if(!created)
				{
					throw new Exception("Error creating directory");
				}
			}
		}
	}
}
