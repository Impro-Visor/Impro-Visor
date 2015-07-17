/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.data;

import java.util.Random;

/**
 *
 * @author muddCS15
 */
public class ContourGenerator{
    
    //more likely to have stretches of the same direction
    //same: 11 or 00
    //different: 11, 00, 10, 01
    //probabilities:
    //11: .25 + .125
    //00: .25 + .125
    //10: .125
    //01: .125
    
    private int n;
    private boolean debug = false;
    
    //private static final int same = 0;
    
    private static final int AA = 0;
    private static final int AB = 1;
    private static final int BA = 2;
    private static final int BB = 3;
    
    
    //makes a contour string that is 2^n long
    public ContourGenerator(int n){
        this.n = n-1;
    }
    
    public String contour(){
        String contour = production(n);
        System.out.println(contour);
        return contour;
    }
    
//    public String contour(){
//        return contour(n);
//    }
    
//    public String contour(int n){
//        if(n==0){
//            System.out.println("base case");
//            return Integer.toString(random());
//        }
//        n--;
//        if(random() == same){
//            System.out.println("same");
//            String contour = contour(n);
//            return contour+contour;
//        }else{
//            System.out.println("different");
//            return contour(n)+contour(n);
//        }
//    }
    
    public String A(int n){
        if(debug){
            System.out.println("A");
        }
        if(n==0){
            if(debug){
               System.out.println("1"); 
            }
            
            return "1";
        }
        n--;
        return production(n);
    }
    
    public String B(int n){
        if(debug){
            System.out.println("B");
        }
        
        if(n==0){
            if(debug){
               System.out.println("0"); 
            }
            
            return "0";
        }
        n--;
        return production(n);
    }
    
    private String production(int n){
        int production = random();
        String toReturn = "";
        switch(production){
            case AA:
                if(debug){
                   System.out.println("(Start AA"); 
                }
                
                String A = A(n);
                toReturn = A+A;
                
                if(debug){
                    System.out.println("End AA)");
                }
                
                break;
            case AB:
                if(debug){
                    System.out.println("(Start AB");
                }
                
                toReturn = A(n)+B(n);
                
                if(debug){
                   System.out.println("End AB)"); 
                }
                break;
            case BA:
                if(debug){
                    System.out.println("(Start BA");
                }
                
                toReturn = B(n)+A(n);
                
                if(debug){
                   System.out.println("End BA)"); 
                }
                
                break;
            case BB:
                if(debug){
                    System.out.println("(Start BB");
                }
                
                String B = B(n);
                toReturn = B+B;
                
                if(debug){
                    System.out.println("End BB)");
                }
                
                break;
        }
        return toReturn;
    }
    
    public int random(){
        Random r = new Random();
        return r.nextInt(4);
    }
    
}
