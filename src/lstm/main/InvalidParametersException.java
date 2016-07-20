/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lstm.main;

/**
 *
 * @author cssummer16
 */
public class InvalidParametersException extends Exception {
    String[] invalid_parameters;
    
    public InvalidParametersException(String[] parameters){
        invalid_parameters = parameters;
    }
}
