/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ratchetgx.orion.widgets.photo;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author hrfan
 */
public class PhotoUtil {
	
	public static String defaultContentType = "application/octet-stream";

    public static boolean supportType(String suffix) {
        String[] fileTypes = {"jpg", "jpeg", "gif", "png"};
        for (int i = 0; i < fileTypes.length; i++) {
            if (fileTypes[i].toLowerCase().equals(suffix)) {
                return true;
            }
        }
        return false;
    }

    public static Map<String, String> getSuffixAndContentTypeMap() {
        Map<String, String> contentTypes = new HashMap<String, String>();

        contentTypes.put("jpg", "image/jpeg");
        contentTypes.put("jpeg", "image/jpeg");
        contentTypes.put("gif", "image/gif");
        contentTypes.put("png", "image/x-png");

        return contentTypes;
    }
}
