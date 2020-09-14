/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.quickstarts.servlet;


import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

///**
// * Definition of the two JMS destinations used by the quickstart
// * (one queue and one topic).
// */
//@JMSDestinationDefinitions(
//    value = {
//        @JMSDestinationDefinition(
//            name = "java:/queue/HelloWorldMDBQueue",
//            interfaceName = "javax.jms.Queue",
//            destinationName = "HelloWorldMDBQueue"
//        ),
//        @JMSDestinationDefinition(
//            name = "java:/topic/HelloWorldMDBTopic",
//            interfaceName = "javax.jms.Topic",
//            destinationName = "HelloWorldMDBTopic"
//        )
//    }
//)

/**
 * <p>
 * A simple servlet 3 as client that sends several messages to a queue or a topic.
 * </p>
 *
 * <p>
 * The servlet is registered and mapped to /HelloWorldMDBServletClient using the {@linkplain WebServlet
 *
 * @author Serge Pagop (spagop@redhat.com)
 * @HttpServlet}. </p>
 */
@ApplicationScoped
@WebServlet("/HelloWorldMDBServletClient")
public class HelloWorldMDBServletClient extends HttpServlet {

    private static final long serialVersionUID = -8314035702649252239L;

    private static final int MSG_COUNT = 5;

    @Resource(lookup = "java:/queue/HelloWorldMDBQueue")
    private Queue queue;

    @Resource(lookup = "java:/topic/HelloWorldMDBTopic")
    private Topic topic;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        Connection connection = null;
        try {
            InitialContext ic = new InitialContext();
            // Now we'll look up the connection factory from which we can create connections to embedded broker:
            ConnectionFactory cf = (ConnectionFactory) ic.lookup("java:jboss/DefaultJMSConnectionFactory");

            // We create a JMS connection using the connection factory:
            connection = cf.createConnection();

            // And we create a non transacted JMS Session, with AUTO_ACKNOWLEDGE acknowledge mode:
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // We create a MessageProducer that will send orders to the queue:
            MessageProducer producer = session.createProducer(queue);

            // We make sure we start the connection, or delivery won't occur on it:
            connection.start();

            for (int i = 0; i < 5; i++) {
                System.out.println("Send a message id: " + i);
                // We create a simple TextMessage and send it:
                TextMessage message = session.createTextMessage("This is an order " + i);
                producer.send(message);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            out.write(ex.getMessage());
        } finally {
            out.write("<h1>Quickstart: Example demonstrates the use of <strong>JMS 2.0</strong> and <strong>EJB 3.2 Message-Driven Bean</strong> in JBoss EAP.</h1>");
            out.write("<p><i>Go to your JBoss EAP server console or server log to see the result of messages processing.</i></p>");
            try {
                connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
            if (out != null) {
                out.close();
            }
        }
    }


    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
