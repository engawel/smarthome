package org.eclipse.smarthome.binding.hue.internal.discovery;

import javax.annotation.Generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class JsonParameters {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("internalipaddress")
    @Expose
    private String internalipaddress;
    @SerializedName("macaddress")
    @Expose
    private String macaddress;
    @SerializedName("name")
    @Expose
    private String name;

    /**
     * 
     * @return
     *         The id
     */
    public String getId() {
        return id;
    }

    /**
     * 
     * @param id
     *            The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *         The internalipaddress
     */
    public String getInternalipaddress() {
        return internalipaddress;
    }

    /**
     * 
     * @param internalipaddress
     *            The internalipaddress
     */
    public void setInternalipaddress(String internalipaddress) {
        this.internalipaddress = internalipaddress;
    }

    /**
     * 
     * @return
     *         The macaddress
     */
    public String getMacaddress() {
        return macaddress;
    }

    /**
     * 
     * @param macaddress
     *            The macaddress
     */
    public void setMacaddress(String macaddress) {
        this.macaddress = macaddress;
    }

    /**
     * 
     * @return
     *         The name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *            The name
     */
    public void setName(String name) {
        this.name = name;
    }

}
