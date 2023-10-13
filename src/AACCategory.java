import structures.AssociativeArray;
import structures.KeyNotFoundException;

/**
 * Represents the mappings for a single page of items that should be displayed
 * 
 * @author Wenfei Lin
 * @author Catie Baker
 */
public class AACCategory {
  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  AssociativeArray<String, String> category;
  String categoryName;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Creates a new empty category with the given name
   * 
   * @param name the name of the category
   */
  public AACCategory(String name) {
    this.category = new AssociativeArray<String, String>();
    this.categoryName = name;
  } // AACCategory​(String)

  // +----------------+----------------------------------------------
  // | Public Methods |
  // +----------------+

  /**
   * Adds the mapping of the imageLoc to the text to the category.
   * 
   * @param imageLoc the location of the image to add
   * @param text the text that image maps to
   */
  public void addItem(String imageLoc, String text) {
    this.category.set(imageLoc, text);
  } // addItem(String, String)
  
  /**
   * Returns the name of the category
   * 
   * @return the name of the category
   */
  public String getCategory() {
    return this.categoryName;
  } // getCategory()

  /**
   * Returns the text associated with the given image loc in this category
   * 
   * @param imageLoc the location of the image
   * @return the text associated with the image
   */
  public String getText(String imageLoc) {
    try {
      return this.category.get(imageLoc);
    } catch (KeyNotFoundException knfe) {
      knfe.printStackTrace();
      return "Error: Could not find text";
    }
  } // getText(String)

  /**
   * Determines if the provided image is stored in the category
   * 
   * @param imageLoc the location of the image
   * @return true if it is in the category, false otherwise
   */
  public boolean hasImage(String imageLoc) {
    return this.category.hasKey(imageLoc);
  } // hasImage(String)

  /**
   * the array of image locations
   * 
   * @return the array of image locations
   */
  public String[] getImages() {
    return this.category.getKeysForAAC();
  } // getImages()
} // class AACCategory
