package com.atex.plugins.seoplugin;

public interface MetaDataModelTypeDescription {

   /**
    * @return title of the news article or empty/null to omit.
    */
   public String getTitle();

   /**
    * @return description or lead of article or empty/null to omit.
    */
   public String getDescription();

   /** 
    * @return publication date as long or -1 to omit.
    */
    public long getPublishingDateTime();

   /**
    * A comma-separated list of keywords.
    * 
    * @return comma-separated list of keywords or empty/null to omit.
    */
    public String getKeywords();
}
