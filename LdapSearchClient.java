import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class LdapSearchClient {

    public static void main(String[] args) {
        try {
            // Establish TCP connection to LDAP server
            Socket socket = new Socket("localhost", 3389); // Default LDAP port is 389
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();

            // Send LDAP Bind request (simple bind)
            String bindRequestHex = "3027 0201 0160 2202 0103 0414 636e 3d44 6972 6563 746f 7279 204d 616e 6167 6572 8007 6368 616e 6461 6e";
            byte[] bindRequestData = hexStringToByteArray(bindRequestHex.replaceAll("\\s", ""));
            outputStream.write(bindRequestData);
            outputStream.flush();

            // Read server response to Bind request
            byte[] responseBuffer = new byte[4096];
            int bytesRead = inputStream.read(responseBuffer);
            if (bytesRead > 0) {
                System.out.println("Bind Response: " + bytesToHex(responseBuffer, bytesRead));
            }

            // Send LDAP Search request
            String searchRequestHex = "3031 0201 0263 2c04 0c64 633d 6974 2c64 633d 636f 6d0a 0102 0a01 0002 0100 0201 0001 0100 870b 6f62 6a65 6374 636c 6173 7330 00";
            byte[] searchRequestData = hexStringToByteArray(searchRequestHex.replaceAll("\\s", ""));
            // System.out.println("searchRequestData : "+Arrays.toString(searchRequestData));
            outputStream.write(searchRequestData);
            outputStream.flush();

            // Read server response to Search request
            while ((bytesRead = inputStream.read(responseBuffer)) != -1) {
                // System.out.println("search REsponse: "+responseBuffer);
                System.out.println("Search Response: " + bytesToHex(responseBuffer, bytesRead));
                break; // Remove this break to read all responses
            }

            // Close the connection
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Utility method to convert hex string to byte array
    public static byte[] hexStringToByteArray(String s) {
        s = s.replaceAll("\\s", ""); // Remove all whitespace
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    // Utility method to convert bytes to hexadecimal string
    public static String bytesToHex(byte[] bytes, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(String.format("%02x", bytes[i]));
        }
        return sb.toString();
    }
    
}
