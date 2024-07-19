package ohhtml.toc;

import java.util.List;

public interface IPage {
    
    List<String> getHeadingHelpKeys(String lang, String headingTitle);
    
    String getContent(String lang);
 
    List<HelpKeysForHeading> getHkh();
    
    void clearHkh();
    
    int getTocHeadingsLevels();
}
