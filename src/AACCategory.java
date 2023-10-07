import structures.AssociativeArray;
import structures.KeyNotFoundException;

/**
 * Represents the mappings for a single page of items that should be displayed
 * 
 * @author Wenfei Lin
 * @author Catie Baker
 */
public class AACCategory {
  // Fields

  AssociativeArray<String, String> category;
  String categoryName;

  // Constructor

  /**
   * Creates a new empty category with the given name
   * 
   * @param name the name of the category
   */
  public AACCategory(String name) {
    // STUB
  } // AACCategory​(String)

  // Methods

  /**
   * Adds the mapping of the imageLoc to the text to the category.
   * 
   * @param imageLoc the location of the image to add
   * @param text the text that image maps to
   */
  public void addItem(String imageLoc, String text) {
    // STUB
  } // addItem(String, String)
  
  /**
   * Returns the name of the category
   * 
   * @return the name of the category
   */
  public String getCategory() {
    return null; // STUB
  } // getCategory()

  /**
   * Returns the text associated with the given image loc in this category
   * 
   * @param imageLoc the location of the image
   * @return the text associated with the image
   */
  public String getText(String imageLoc) {
    return null; // STUB
  } // getText(String)

  /**
   * Determines if the provided images is stored in the category
   * 
   * @param imageLoc the location of the category
   * @return true if it is in the category, false otherwise
   */
  public boolean hasImage(String imageLoc) {
    return false; // STUB
  } // hasImage(String)

  /**
   * the array of image locations
   * 
   * @return the array of image locations
   */
  public String[] getImages() {
    return new String[0];// STUB
  } // getImages()
} // class AACCategory
