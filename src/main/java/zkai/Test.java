/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zkai;

import java.io.IOException;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;

/**
 *
 * @author user
 */
public class Test{
    
    public static void main(String args[]) throws Exception {
        Server server = new Server();
        server.start();
    }
}



