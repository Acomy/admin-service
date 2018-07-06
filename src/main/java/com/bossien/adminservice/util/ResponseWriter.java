package com.bossien.adminservice.util;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ResponseWriter {

    /**
     * @param message
     * @param response
     */
    public static void writeAjaxJSONResponse(String message, HttpServletResponse response) {

        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter writer = getWriter(response);
        writeAjaxJSONResponse(message, writer);
    }
    /**
     *
     * @param response
     * @return
     */
    private static PrintWriter getWriter(HttpServletResponse response) {
        if(null == response){
            return null;
        }
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer;
    }

    /**
     * description:send the ajax response back to the client side.
     *
     * @param writer
     * @param writer
     */
    private static void writeAjaxJSONResponse(String message, PrintWriter writer) {
        if (writer == null || message == null) {
            return;
        }
        try {
            writer.write(message);
        } finally {
            writer.flush();
            writer.close();
        }
    }
}
