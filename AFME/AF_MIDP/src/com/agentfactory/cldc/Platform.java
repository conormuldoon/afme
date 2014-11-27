/*
 * Platform.java
 *
 * Created on 03 March 2005, 11:59
 */

package com.agentfactory.cldc;
import com.agentfactory.cldc.logic.FOS;
import com.agentfactory.cldc.mts.IDSet;
/** This interface is implemented by agent platform classes.
 * It defines basic management functionality for persistent
 * storage and displaying the mental state debugger interface.
 *
 * @author Conor Muldoon
 */
public interface Platform {
    // Only ever used for debugging
    
    /**
     * Displays the interface for the platform.
     */
    public void display();
    
    /** Creates a new persistent store.
     * 
     * @param s the name of the store.
     * @return the ID for the platform if it has been registered, null otherwise.
     */
    public String newStore(String s);
    
    /** Adds data to a persistent store.
     * 
     * @param identifier the name of the persistent store.
     * @param s the data to add.
     */
    public void addData(String identifier,String s);
    
    /** Adds an agent name to the name store.
     * 
     * @param name the name of the agent.
     */
    public void storeName(FOS name);
    
    /** Adds an agent ID to the ID store.
     * 
     * @param sb a string representation of the ID to add.
     */
    public void saveID(String sb);
    
    /** Adds IDs from the ID store that are associated with the
     * specified agent name to the ID set.
     * 
     * @param name the specified agent name.
     * @param ids the ID set that the IDs are added to.
     */
    public void addIDs(Object name,IDSet ids);
    
}
