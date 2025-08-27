package br.com.erudio.exception;

import java.util.Date;

public record ExcepetionResponse(Date timestamp, String message, String details) {
}
