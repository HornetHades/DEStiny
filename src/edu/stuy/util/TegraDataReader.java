package edu.stuy.util;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import edu.wpi.first.wpilibj.SerialPort;

public class TegraDataReader {
    private SerialPort serialPort;
    private AtomicReference<double[]> vectorData;
    public static final TegraDataReader reader = new TegraDataReader();

    private TegraDataReader() {
        SerialPort.Port p = SerialPort.Port.kOnboard;
        serialPort = new SerialPort(9600, p, 8, SerialPort.Parity.kNone, SerialPort.StopBits.kOne);
    }

    public double[] readVector() {
        return vectorData.get();
    }   
    public void readVectorFromSerialPort(PrintWriter writer) {
        // Treat writer like an Option type. It exists for testing.
        boolean verbose = writer != null;

        if (verbose) {
            writer.println("About to read a byte people " + System.currentTimeMillis());
            writer.flush();
        }

        // Read 3 doubles, as there are 3 doubles in each
        // vector we are reading
        double[] vector = new double[3];
        for (int i = 0; i < 3; i++) {
            byte[] data = serialPort.read(8);
            // TODO: Test with serialPort.setTimeout rather than
            // this loop
            while (data.length != 8) {
                data = serialPort.read(8);
            }
            vector[i] = Converter.bytesToDouble(data);
        }
        if (verbose) {
            writer.println("double[3] received: " + Arrays.toString(vector));
            writer.flush();
        }
        if (vector[0] == Double.POSITIVE_INFINITY
                && vector[1] == Double.POSITIVE_INFINITY
                && vector[2] == Double.POSITIVE_INFINITY) {
            // Three +Infinitys is the flag for no goal found
            // There is only one double representation of
            // positive infinity
        }
        vectorData.set(vector);
    }

    public void closePort() {
        serialPort.free();
    }
}
