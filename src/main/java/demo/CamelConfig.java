/**
 *  Copyright 2005-2015 Red Hat, Inc.
 *
 *  Red Hat licenses this file to you under the Apache License, version
 *  2.0 (the "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package demo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.RuntimeService;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
//import org.apache.camel.component.metrics.routepolicy.MetricsRoutePolicyFactory;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.spring.javaconfig.CamelConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

@Configuration
public class CamelConfig extends CamelConfiguration {

	@Autowired
	PhotoService photoService;
	
    @Autowired
    private RuntimeService runtimeService;
	
	
    @Bean
    public RouteBuilder route() {
        return new RouteBuilder() {
        	
        	
        	
        	
            @Override
			public void configure() throws Exception {

				Processor myProcessor = new Processor() {
					public void process(Exchange exchange) {
						System.out.println("processing !! " + exchange);
						System.out.println(exchange.getProperties());
						System.out.println(exchange.getIn().getExchange().getProperties());

						exchange.getProperty("photo");
						exchange.setProperty("blah", "blah");
						List<Photo> photos = (List<Photo>) exchange.getProperty("photos");

/*						for (Photo photo : photos) {
							Long photoId = photo.getId();
							System.out.println("integration: handling photo #" + photoId);
							photoService.dogifyPhoto(photo);

						}
						*/
						Gson gson = new Gson();
						
						
						
						MessageEnvelop envelop = new MessageEnvelop();
						
						envelop.setPhotos(photos);
						envelop.setExecutionId(exchange.getProperty("PROCESS_ID_PROPERTY")+"");
						HashMap<String, Object> message = new HashMap<>();
						message.put("photos", photos);
						message.put("PROCESS_ID_PROPERTY",exchange.getProperty("PROCESS_ID_PROPERTY"));
						
						String msg = gson.toJson(envelop);
						
						System.out.println("mdg >>" + msg);
						
						exchange.getOut().setBody(msg);
						
						
						
					}
				};
				
				

				Processor myProcessor2 = new Processor() {
					public void process(Exchange exchange) throws JsonSyntaxException, InvalidPayloadException {
						System.out.println("processing !! " + exchange.getIn() + "  " + exchange.getIn().getMessageId());	
						
						Gson gson = new Gson();
						
						String normalizedStr = StringUtils.removeStart( exchange.getIn() + "  ", "Message: ");
						System.out.println("normalizedStr" + normalizedStr);
						
						MessageEnvelop env = gson.fromJson(normalizedStr, MessageEnvelop.class);
						
						
						
						
						System.out.println("env" + env);
						System.out.println(env.getPhotos());
						System.out.println(env.getExecutionId());
						
						List<Photo> photos = (List<Photo>) env.getPhotos();

						
						
						sleepForSomeTime();
						
						for (Photo photo : photos) {
							Long photoId = photo.getId();
							System.out.println("integration: handling photo #" + photoId);
							photoService.dogifyPhoto(photo);

						}
						
						
						
						runtimeService.signal(env.getExecutionId());
						
						System.out.println("signalled !!!");
						
						}

					private void sleepForSomeTime() {
						
						
						System.out.println("seconds left for dogification >>" );
						
						int j = 60;
						
						for (int i = 0; i < 20; i++) {
							
							try {
								Thread.sleep(1000);
								System.out.print((j-1) + " ");
								
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}
						
						
						
					}				
				};
				
				
				

				from("activiti:photoProcess:theCamelTask")
						// .lounmarshal(jaxb)
						.log("Testing Camel route invocation from a Camel task. ${property.photo}").process(myProcessor)
						//.setBody(body().append("Hello World!"))
						.to("rabbitmq:localhost:5672/tasks?username=guest&password=guest&autoDelete=false&routingKey=camel&queue=task_queue");
				// .to("mock:result");

				from("rabbitmq:localhost:5672/tasks?username=guest&password=guest&autoDelete=false&routingKey=camel&queue=task_queue")
						// .lounmarshal(jaxb)
						.log("message recieved... ${in.body}").process(myProcessor2);

			}             
            
            
        };
    }

    @Override
    protected void setupCamelContext(CamelContext camelContext) throws Exception {
        // make Camel aware of Spring Boot’s application.properties
        PropertiesComponent pc = new PropertiesComponent();
        pc.setLocation("classpath:application.properties");
        camelContext.addComponent("properties", pc);
       
        

        // enable performance metrics
       // camelContext.addRoutePolicyFactory(new MetricsRoutePolicyFactory());

        super.setupCamelContext(camelContext);
    }
}
