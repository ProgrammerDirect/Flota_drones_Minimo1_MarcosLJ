package edu.upc.dsa.exceptions;

public class DronEnMantenimientoException extends Exception
{
    public DronEnMantenimientoException(String message)
    {
        super("Dron en mantenimiento: " + message);
    }
}
