package de.zalando.zomcat.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author  henning
 */
public class ManifestServlet extends HttpServlet {

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException,
        IOException {

        ServletContext application = getServletConfig().getServletContext();
        InputStream inputStream = application.getResourceAsStream("/META-INF/MANIFEST.MF");
        Manifest manifest = new Manifest(inputStream);

        resp.setContentType("text/plain");

        final PrintWriter writer = resp.getWriter();
        final List<String> lines = new ArrayList<String>();

        for (Entry<Object, Object> attr : manifest.getMainAttributes().entrySet()) {
            lines.add(attr.getKey() + ": " + attr.getValue());
        }

        Collections.sort(lines);
        for (String line : lines) {
            writer.println(line);
        }

        writer.println();

        for (Entry<String, Attributes> entry : manifest.getEntries().entrySet()) {
            writer.println(entry.getKey());
            for (Entry<Object, Object> attr : entry.getValue().entrySet()) {
                writer.println(attr.getKey() + ": " + attr.getValue());
            }
        }

        writer.flush();
        writer.close();
    }
}
