/*******************************************************************************
 * Micha³ Motoszko
 * 2018-11-27
 * Grafika komputerowa PS 9
 * Projekt 9
 ******************************************************************************/
package com.mmotoszko.gk.project9.file;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import com.mmotoszko.gk.project9.drawing.Functions2D;
import com.mmotoszko.gk.project9.drawing.Point2D;
import com.mmotoszko.gk.project9.drawing.customshape.CustomImage;

public class FileConverter {
	public static int READ_BUFFER_SIZE_BYTES = 1024;

	public static FileExtension getFileExtension(File file) {
		char[] fileName = file.getName().toCharArray();

		int i;
		for (i = fileName.length - 1; i >= 0; i--) {
			if (fileName[i] == '.') {
				break;
			}
		}

		FileExtension fileExtension = FileExtension.OTHER;
		String extension = file.getName().substring(i + 1);

		if (FileExtension.JPEG.toString().equalsIgnoreCase(extension)
				|| FileExtension.JPG.toString().equalsIgnoreCase(extension)) {
			fileExtension = FileExtension.JPG;
		} else if (FileExtension.PPM.toString().equalsIgnoreCase(extension)) {
			fileExtension = FileExtension.PPM;
		}else if (FileExtension.MMGE.toString().equalsIgnoreCase(extension)) {
			fileExtension = FileExtension.MMGE;
		}

		return fileExtension;
	}

	public static String getLiteralFileExtension(File file) {
		char[] fileName = file.getName().toCharArray();

		int i;
		for (i = fileName.length - 1; i >= 0; i--) {
			if (fileName[i] == '.') {
				break;
			}
		}

		String extension = file.getName().substring(i);

		return extension;
	}

	public static boolean isJPEG(File file) {
		return FileExtension.JPEG == getFileExtension(file) || FileExtension.JPG == getFileExtension(file);
	}
	
	public static boolean isMMGE(File file) {
		return FileExtension.MMGE == getFileExtension(file);
	}

	public static boolean isPPM(File file) {
		return FileExtension.PPM == getFileExtension(file);
	}

	public static boolean canBeRead(File file) {
		return FileExtension.OTHER != getFileExtension(file);
	}

	public static CustomImage getImage(File file, Point2D location) {
		CustomImage image = null;

		if (isPPM(file)) {
			image = getPpm(file, location);
		} else if (isJPEG(file)) {
			image = getJpeg(file, location);
		}

		return image;
	}

	private static CustomImage getJpeg(File file, Point2D location) {
		// decode jpeg here vvv
		BufferedImage img = null;

		try {
			img = ImageIO.read(file);
		} catch (IOException e1) {
			System.out.println("Failed to retreive image from jpeg. Details: " + e1.getMessage());
		}

		if (img != null) {

			int graphicWidth = img.getWidth();
			int graphicHeight = img.getHeight();
			byte[] graphic = new byte[graphicWidth * graphicHeight * 3];

			int index = 0;
			for (int row = 0; row < graphicHeight; row++) {
				for (int col = 0; col < graphicWidth; col++) {
					Color pixel = new Color(img.getRGB(col, row));
					graphic[index] = (byte) pixel.getRed();
					graphic[index + 1] = (byte) pixel.getGreen();
					graphic[index + 2] = (byte) pixel.getBlue();
					index += 3;
				}
			}
			// decode jpeg here ^^^

			try {
				return new CustomImage(new Point2D(location), graphicWidth, graphicHeight, graphic, graphicWidth,
						graphicHeight);
			} catch (Exception e) {
				System.out.println("This should not happen.");
			}
		}
		return null;
	}

	private static boolean isMeaningfulPpmChar(byte value) {
		return isDigit((char) value) || isWhitespace((char) value) || (char) value == '#' || (char) value == 'P';
	}

	private static boolean isDigit(char value) {
		return value == '0' || value == '1' || value == '2' || value == '3' || value == '4' || value == '5'
				|| value == '6' || value == '7' || value == '8' || value == '9';
	}

	private static boolean isWhitespace(char value) {
		return value == 32 || value == 9 || value == 10 || value == 13 || value == 12 || value == 11;
	}

	private static int getIntFromBytes(byte[] values) {
		if (values.length == 0) {
			return 0;
		}

		int intValue = ((int) values[0] & 0xFF);
		for (int i = 1; i < values.length; i++) {
			intValue = (intValue << 8) | ((int) values[i] & 0xFF);
		}

		return intValue;
	}

	private static int getByteSize(int value) {
		if (value < 256) {
			return 1;
		} else if (value < 65536) {
			return 2;
		} else if (value < 16777216) {
			return 3;
		} else {
			int counter = 0;
			while (value != 1) {
				value /= 2;
				counter++;
			}
			return counter / 8 + 1;
		}

	}

	private static CustomImage getPpm(File file, Point2D location) {
		// decode ppm here

		String ppmType = null;
		int graphicWidth = 0;
		int graphicHeight = 0;
		int maximumValue = 0;
		int byteValuesShift = 0;
		List<Byte> graphicList = new ArrayList<>();

		try {
			// Reading the whole file as binary - later to be decoded
			byte[] fullFile = Files.readAllBytes(Paths.get(file.getAbsolutePath()));

			List<Character> readChars = new ArrayList<>();

			// Extracting PPM descriptor - PPM type, width, height, maximum
			// value
			boolean gotDescriptors = false;
			boolean readingComment = false;
			int i;
			for (i = 0; i < fullFile.length; i++) {
				if (isMeaningfulPpmChar(fullFile[i])) {
					if ((char) fullFile[i] == '#') {
						readingComment = true;
					} else if ((char) fullFile[i] == '\n') {
						readingComment = false;
						readChars.add(' ');
					} else if (!readingComment) {
						readChars.add((char) fullFile[i]);
					}
				}

				int whitespaces = 0;
				boolean sameWhitespace = false;
				String tempPpmType = "", width = "", height = "", maxValue = "";
				for (int j = 0; j < readChars.size(); j++) {
					if (isWhitespace(readChars.get(j))) {
						if (!sameWhitespace) {
							whitespaces++;
							sameWhitespace = true;
							if (whitespaces == 4) {
								ppmType = tempPpmType;
								graphicWidth = Integer.parseInt(width);
								graphicHeight = Integer.parseInt(height);
								maximumValue = Integer.parseInt(maxValue);
								byteValuesShift = i + 1;
								gotDescriptors = true;
								break;
							}
						}
					} else {
						sameWhitespace = false;
						if (whitespaces == 0) {
							tempPpmType += readChars.get(j);
						} else if (whitespaces == 1) {
							width += readChars.get(j);
						} else if (whitespaces == 2) {
							height += readChars.get(j);
						} else if (whitespaces == 3) {
							maxValue += readChars.get(j);
						}
					}
				}
				if (gotDescriptors) {
					break;
				}
			}

			// System.out.print(ppmType + "\nw/h: " + graphicWidth + "/" +
			// graphicHeight + "\nmax: " + maximumValue
			// + "\nshift: " + byteValuesShift + "\n");

			int dataSizeInBytes = getByteSize(maximumValue);
			byte[] data = new byte[dataSizeInBytes];

			if ("P6".equalsIgnoreCase(ppmType)) {
				// Constructing graphic from bytes
				System.out.println("converting P6");

				int dataIndex = 0;
				for (i = byteValuesShift; i < fullFile.length; i++) {
					data[dataIndex] = fullFile[i];
					dataIndex++;

					if (dataIndex == dataSizeInBytes) {
						dataIndex = 0;
						graphicList.add(Functions2D.getByteValue(getIntFromBytes(data), maximumValue));
					}
				}
				graphicList.add(Functions2D.getByteValue(getIntFromBytes(data), maximumValue));

			} else if ("P3".equalsIgnoreCase(ppmType)) {
				// Constructing numbers digit by digit
				System.out.println("converting P3");

				int readNumber = 0;
				boolean dataPointReady = false;
				char currentChar;

				for (i = byteValuesShift; i < fullFile.length / dataSizeInBytes; i++) {
					for (int j = 0; j < dataSizeInBytes; j++) {
						currentChar = (char) fullFile[i * dataSizeInBytes + j];
						if (currentChar == '#') {
							readingComment = true;
						} else if (currentChar == '\n') {
							readingComment = false;
						}
						if (!readingComment) {
							if (i == fullFile.length - 1 && isDigit(currentChar)) {
								readNumber *= 10;
								readNumber += currentChar - '0';
							} else if (i > 0 && isWhitespace(currentChar)
									&& isWhitespace((char) fullFile[i * dataSizeInBytes + j - 1])) {
								continue;
							} else if (isDigit(currentChar)
									&& isWhitespace((char) fullFile[i * dataSizeInBytes + j + 1])) {
								readNumber *= 10;
								readNumber += currentChar - '0';
								dataPointReady = true;
							} else if (isDigit(currentChar)) {
								readNumber *= 10;
								readNumber += currentChar - '0';
							}
						}

						if (dataPointReady) {
							dataPointReady = false;
							graphicList.add(Functions2D.getByteValue(readNumber, maximumValue));
							readNumber = 0;
						}
					}
				}
				graphicList.add(Functions2D.getByteValue(readNumber, maximumValue));
			}
		} catch (IOException e1) {
			System.out.println("IO error. Details:" + e1.getMessage());
		}

		byte[] graphic = new byte[graphicList.size()];

		for (int i = 0; i < graphicList.size(); i++) {
			graphic[i] = graphicList.get(i);
		}
		// decode ppm here

		try {
			return new CustomImage(new Point2D(location), graphicWidth, graphicHeight, graphic, graphicWidth,
					graphicHeight);
		} catch (Exception e) {
			System.out.println("This should not happen.");
		}
		return null;
	}
}
