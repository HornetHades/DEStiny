package edu.stuy.util;

import java.io.PrintWriter;

public class CvDataRetrievalThread implements Runnable {

	@Override
	public void run() {
		TegraDataReader reader = TegraDataReader.reader;
		 reader.readVectorFromSerialPort(null);
		}
	}
