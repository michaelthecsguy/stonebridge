package com.sharplabs.Enum;

/**
 * Created by yangm on 8/27/15.
 */
public enum Environment
{
  LOCAL("local", "https://localhost:8080/"),
  DEV("dev", "https://usdev-dm.sharpcloudportal.com/"),
  USQA3DM1("usqa3dm1", "https://usqa3-dm1.sharpcloudportal.com/"),
  USQA3DM2("usqa3dm2", "https://usqa3-dm.sharpcloudportal.com/"),
  USPRODDM1("usproddm1", "https://"),
  USPRODDM2("usproddm2","https://");

  private String envName, url;

  Environment(String envName, String url)
  {
    this.envName = envName;
    this.url = url;
  }

  public String getEnvName() {
    return envName;
  }

  public String getUrl() {
    return url;
  }

  public static Environment findByEnvName(String envName)
  {
    if (envName != null)
      for (Environment env : values())
      {
        if (env.getEnvName().equals(envName))
          return env;
      }

    return null;
  }

}
