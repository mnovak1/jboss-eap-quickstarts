/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.as.quickstarts.ejb_security;

import javax.ejb.EJBAccessException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;

/**
 *
 * @author psotirop
 */
@WebServlet(name = "RemoteClientServlet", urlPatterns = {"/"})
public class RemoteClientServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            boolean hasAdminPermission = false;
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet RemoteClientServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet RemoteClientServlet at " + request.getContextPath() + "</h1>");
            hasAdminPermission=callRemoteEjb();
            out.println("\n\n\n* * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n\n");
            out.println("\nPrincipal has user role: " + hasAdminPermission);
            out.println("\n\n* * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n\n\n");
            out.println("</body>");
            out.println("</html>");
        } catch (NamingException ex) {
            ex.printStackTrace();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private boolean callRemoteEjb() throws NamingException {
        String host = "localhost";
        String port = "8080";
        if(System.getenv("SERVER2_HOST")!=null)
            host=System.getenv("SERVER2_HOST");
        if(System.getenv("SERVER2_PORT")!=null)
            port=System.getenv("SERVER2_PORT");
        boolean hasAdminPermission = false;
        final Hashtable<String, String> jndiProperties = new Hashtable<>();
            jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
            jndiProperties.put(Context.PROVIDER_URL, "remote+http://" + host + ":" + port);
        final Context context = new InitialContext(jndiProperties);

        SecuredEJBRemote reference = (SecuredEJBRemote) context.lookup("ejb:/ROOT/SecuredEJB!"
                    + SecuredEJBRemote.class.getName());


        try {
            hasAdminPermission = reference.administrativeMethod();
        } catch (EJBAccessException e) {
            e.printStackTrace();
        }finally{
            return hasAdminPermission;
        }

    }

}
