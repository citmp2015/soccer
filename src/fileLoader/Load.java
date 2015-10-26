package fileLoader;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by Jan Leupolt on 25.10.15.
 */
public class Load {
    private static Load load = null;

    public Load getInstance(){
        if(load==null){
            load = new Load();
        }
        return load;
    }
    public Load(){

    }
    public static void loadSoccerFile(String filePath) throws IOException {
        FileInputStream inputStream = null;
        Scanner sc = null;
        try {
            inputStream = new FileInputStream(filePath);
            sc = new Scanner(inputStream, "UTF-8");
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                System.out.println(line);
            }
            if (sc.ioException() != null) {
                throw sc.ioException();
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (sc != null) {
                sc.close();
            }
        }
    }
}
