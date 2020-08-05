/*
 * This NMEA 0183 parser without any modifications can be found at https://gist.github.com/javisantana/1326141 and is
 * licensed under the MIT Open Source license.
 *
 * Copyright <2013> <Javi Santana>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package GPS_Simulator;
import java.util.HashMap;
import java.util.Map;

public class NMEA {

    interface SentenceParser {
        void parse(String[] tokens, GPSPosition position);
    }

    static float Latitude2Decimal(String latitude, String NS) {
        float med = Float.parseFloat(latitude.substring(2))/60.0f;
        med +=  Float.parseFloat(latitude.substring(0, 2));
        if(NS.startsWith("S")) {
            med = -med;
        }
        return med;
    }

    static float Longitude2Decimal(String longitude, String WE) {
        float med = Float.parseFloat(longitude.substring(3))/60.0f;
        med +=  Float.parseFloat(longitude.substring(0, 3));
        if(WE.startsWith("W")) {
            med = -med;
        }
        return med;
    }

    static class GPGGA implements SentenceParser {
        public void parse(String [] tokens, GPSPosition position) {
            position.time = Float.parseFloat(tokens[1]);
            position.latitude = Latitude2Decimal(tokens[2], tokens[3]);
            position.longitude = Longitude2Decimal(tokens[4], tokens[5]);
            position.quality = Integer.parseInt(tokens[6]);
            position.altitude = Float.parseFloat(tokens[9]);
        }
    }

    static class GPGGL implements SentenceParser {
        public void parse(String [] tokens, GPSPosition position) {
            position.latitude = Latitude2Decimal(tokens[1], tokens[2]);
            position.longitude = Longitude2Decimal(tokens[3], tokens[4]);
            position.time = Float.parseFloat(tokens[5]);
        }
    }

    static class GPRMC implements SentenceParser {
        public void parse(String [] tokens, GPSPosition position) {
            position.time = Float.parseFloat(tokens[1]);
            position.latitude = Latitude2Decimal(tokens[3], tokens[4]);
            position.longitude = Longitude2Decimal(tokens[5], tokens[6]);
            position.velocity = Float.parseFloat(tokens[7]);
            position.dir = Float.parseFloat(tokens[8]);
        }
    }

    static class GPVTG implements SentenceParser {
        public void parse(String [] tokens, GPSPosition position) {
            position.dir = Float.parseFloat(tokens[3]);
        }
    }

    static class GPRMZ implements SentenceParser {
        public void parse(String [] tokens, GPSPosition position) {
            position.altitude = Float.parseFloat(tokens[1]);
        }
    }

    public static class GPSPosition {
        public float time = 0.0f;
        public float latitude = 0.0f;
        public float longitude = 0.0f;
        public boolean fixed = false;
        public int quality = 0;
        public float dir = 0.0f;
        public float altitude = 0.0f;
        public float velocity = 0.0f;

        public void updatefix() {
            fixed = quality > 0;
        }

        public String toString() {
            return String.format("POSITION: latitude: %f, longitude: %f, time: %f, Q: %d, dir: %f, alt: %f, vel: %f", latitude, longitude, time, quality, dir, altitude, velocity);
        }
    }

    static GPSPosition position = new GPSPosition();

    private static final Map<String, SentenceParser> sentenceParsers = new HashMap<>();

    public NMEA() {
        sentenceParsers.put("GPGGA", new GPGGA());
        sentenceParsers.put("GPGGL", new GPGGL());
        sentenceParsers.put("GPRMC", new GPRMC());
        sentenceParsers.put("GPRMZ", new GPRMZ());
        sentenceParsers.put("GPVTG", new GPVTG());
    }

    public static void parse(String line) {

        if(line.startsWith("$")) {
            String nmea = line.substring(1);
            String[] tokens = nmea.split(",");
            String type = tokens[0];
            if(sentenceParsers.containsKey(type)) {
                sentenceParsers.get(type).parse(tokens, position);
            }
            position.updatefix();
        }

    }
}
