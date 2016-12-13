/*
We need 3 things:
1.) ApplicationEvent
2.) ApplicationEventPublisher
3.) ApplicationListener

Spring uses ApplicationEvent as means of communication between container managed beans.

A bean which wants to let other beans know about the occurrence of a certain event will act as publisher to publish the event. Likewise, a bean which 
is interested to know about the event will implement ApplicationListener and become the listener.

The bean doesn�t have to explicitly subscribe itself. All it needs to do is implement ApplicationListener and register itself in the context XML. Once 
the application context publishes the event, spring will automatically publish the event to all the ApplicationListener(s). Using events, an event 
publisher object can communicate with other objects without even knowing which objects are listening. Likewise, an event listener can react to events 
without knowing which object published the events.

The ApplicationContext interface contains the publishEvent() method which publishes the ApplicationEvents. Any ApplicationListener that is registered 
in the application context will receive the event in a call to its onApplicationEvent() method.

In order to publish events, your bean needs to have access to the ApplicationContext. This means that beans will have to be made aware of the container 
that they�re running in. For this, you need to make the publisher implement ApplicationContextAware interface.

----------
Publisher:
----------
This is achieved via the�ApplicationEventPublisher�interface.
public interface ApplicationEventPublisher {
    void publishEvent(ApplicationEvent event);
}

The interface exposes a single method - one that allows us to publish events. The ApplicationContext implements the above interface. 

---------
Listener:
---------
1.) For listening to events that are published, a class needs to implement the�ApplicationListener�interface.
public class AllApplicationEventListener implements  ApplicationListener<ApplicationEvent> {
	@Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) { ... }

2.) The above class listens for�all events generated by the ApplicationContext. The presence of generics in the Listener interface allows us to 
listen for very specific events. The ContextStartedEvent is one of the events generated by the ApplicationContext. It extends the�generic (and 
abstract) ApplicationEvent. Let us look at one more Listener that listens for this event only:
public class ContextStartedEventListener implements ApplicationListener<ContextStartedEvent> {
	@Override
    public void onApplicationEvent(ContextStartedEvent event) { ... }
    
3.) Spring is not restricted to using these predefined events. It allows us to define and publish Custom events too.

 */

package _001;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class _039_ApplicationEvent {
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("_039_ApplicationEvent.xml");
		
		// start() creates ContextStartedEvent() and publish it.
		((AbstractApplicationContext) context).start();    		// Let us raise a start event.		
		((AbstractApplicationContext) context).refresh();    	// Let us raise a refresh event.  		
		((AbstractApplicationContext) context).stop();    		// Let us raise a stop event.  
		((AbstractApplicationContext) context).close();    		// Let us raise a close event. 
	}
}

//this listener will listen for all published events
class GenericEventListener implements ApplicationListener<ApplicationEvent>{
	public void onApplicationEvent(ApplicationEvent event) {
		System.out.println(event.getSource().getClass() + " recieved");		
	}
}

//this listener will listen for only ContextStartedEvent
class ContextStartedEventListener implements ApplicationListener<ContextStartedEvent>{
	public void onApplicationEvent(ContextStartedEvent event) {
		System.out.println("ContextStartedEvent recieved");		
	}
}

//this listener will listen for only ContextRefreshedEvent
class ContextRefreshedEventListener implements ApplicationListener<ContextRefreshedEvent>{
	public void onApplicationEvent(ContextRefreshedEvent event) {
		System.out.println("ContextRefreshedEvent recieved");		
	}
}

//this listener will listen for only ContextStoppedEvent
class ContextStoppedEventListener implements ApplicationListener<ContextStoppedEvent>{
	public void onApplicationEvent(ContextStoppedEvent event) {
		System.out.println("ContextStoppedEvent recieved");		
	}
}

//this listener will listen for only ContextClosedEvent
class ContextClosedEventListener implements ApplicationListener<ContextClosedEvent>{
	public void onApplicationEvent(ContextClosedEvent event) {
		System.out.println("ContextClosedEvent recieved");		
	}
}