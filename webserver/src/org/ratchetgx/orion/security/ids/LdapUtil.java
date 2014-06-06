package org.ratchetgx.orion.security.ids;

public class LdapUtil {

    /**
     * 从用户DN解析出用户ID.
     */
    public static String parserUserDN(String dn) {
        if (dn == null) {
            return null;
        }
        String[] arr1 = dn.split(",");
        for (int i = 0; i < arr1.length; i++) {
            String[] arr2 = arr1[i].split("=");
            if (arr2.length == 2) {
                if ("uid".equalsIgnoreCase(arr2[0])) {
                    return arr2[1];
                }
            }
        }
        return null;
    }
}
