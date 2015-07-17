/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.data;

/**
 *
 * @author muddCS15
 */
public class ContourGeneratorTest {
    
    public static void main(String [] args){
        ContourGenerator generator = new ContourGenerator(2);
        System.out.println(generator.contour());
    }
    
}
