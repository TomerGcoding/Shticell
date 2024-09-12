package com.shticell.engine.exceptions;

public class IllegalNumberOfArgumentsException extends RuntimeException{
     private static final long serialVersionUID = 1L;
     public IllegalNumberOfArgumentsException(){
         super();
     }
     public IllegalNumberOfArgumentsException(String message){
         super(message);
     }
     public IllegalNumberOfArgumentsException(String message, Throwable cause){
         super(message, cause);
     }
     public IllegalNumberOfArgumentsException(Throwable cause){
         super(cause);
     }
}
