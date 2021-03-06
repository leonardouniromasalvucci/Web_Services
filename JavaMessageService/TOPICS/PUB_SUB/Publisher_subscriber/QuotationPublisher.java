/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.softeng.jms2server;

/**
 *
 * @author leona
 */

import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.*;
import javax.jms.*;

/**
 *
 * @author biar
 */
public class QuotationPublisher {

    private final String quotations[] = {"Telecom", "Finmeccanica", "Banca_Intesa",
        "Oracle", "Parmalat", "Mondadori", "Vodafone", "Barilla"};
    private Random randomGen;
    private Properties props;
    private InitialContext jndiContext;
    private TopicConnectionFactory cf;
    private TopicConnection connection;
    private TopicSession session;
    private Topic dest;
    private TopicPublisher producer;

    public QuotationPublisher() {
        randomGen = new Random();
        //look up for the connection factory
        props = new Properties();
        props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
        jndiContext = null;
        cf = null;
        connection = null;
        session = null;
        dest = null;
        producer = null;
        try {
            jndiContext = new InitialContext(props);
            cf = (TopicConnectionFactory) jndiContext.lookup("ConnectionFactory");          
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void start() {
        try {
            connection = cf.createTopicConnection();
            session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            dest = (Topic) jndiContext.lookup("dynamicTopics/Quotations");
            producer = session.createPublisher(dest);
            connection.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
            TextMessage mess = null;
            while (true) {
                try {
                    mess = session.createTextMessage();
                    mess.setStringProperty("Quotation", quotations[randomGen.nextInt(this.quotations.length)]);
                    mess.setDoubleProperty("Price", randomGen.nextDouble() * this.quotations.length * 10);
                    mess.setStringProperty("Test", "ciaoo");
                    try{
                        Thread.sleep(8000);
                        producer.send(mess);
                    }catch(Exception ex){}                  
                   
                    
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        

    }
}
