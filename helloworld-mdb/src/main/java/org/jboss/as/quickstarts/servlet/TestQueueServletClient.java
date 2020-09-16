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
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

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
@WebServlet("/TestQueueServletClient")
public class TestQueueServletClient extends HttpServlet {

    private static final long serialVersionUID = -8314035702649252239L;

    private static final int MSG_COUNT = 5;

    @Resource(lookup = "java:/jms/amq/queue/testQueue")
    private Queue queue;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        Connection connection = null;
        try {
            InitialContext ic = new InitialContext();
            // Now we'll look up the connection factory
            ConnectionFactory cf = (ConnectionFactory) ic.lookup("java:/RemoteJmsXA");

            // We create a JMS connection using the connection factory:
            connection = cf.createConnection();

            // And we create a non transacted JMS Session, with AUTO_ACKNOWLEDGE acknowledge mode:
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // We create a MessageProducer that will send orders to the queue:
            MessageProducer producer = session.createProducer(queue);
            MessageConsumer consumer = session.createConsumer(queue);

            // We make sure we start the connection, or delivery won't occur on it:
            connection.start();

            for (int i = 0; i < 5; i++) {
                System.out.println("Send a message id: " + i);
                // We create a simple TextMessage and send it:
                TextMessage message = session.createTextMessage("This is an order " + i);
                producer.send(message);
            }

            for (int i = 0; i < 4; i++) {
                TextMessage receivedMessage = (TextMessage) consumer.receive();
                System.out.println("Got order: " + receivedMessage.getText());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            out.write(ex.getMessage());
        } finally {
            out.write("Send and consume messages to/from " + queue.toString());
            out.write(System.lineSeparator());

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
