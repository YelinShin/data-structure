package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 * 
 */
public class Tree {
	
	/**
	 * Root node
	 */
	TagNode root=null;
	
	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;
	
	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}
	
	/**
	 * Builds the DOM tree from input HTML file. The root of the 
	 * tree is stored in the root field.
	 */
	public void build() {
		Stack<TagNode> tags = new Stack<TagNode>();
		sc.nextLine();
		root = new TagNode("html", null, null);
		tags.push(root); // pushes initial <html> tag into stack
		
		while(sc.hasNextLine()) {
			String str = sc.nextLine();
			Boolean isTag = false;
			if(str.charAt(0) == '<') {
				if(str.charAt(1) == '/') {
					tags.pop();
					continue;
				} else {
					str = str.replace("<", "");
					str = str.replace(">", "");
					isTag = true;
				}
			}
			TagNode temp = new TagNode(str, null, null);
			if(tags.peek().firstChild == null) {
				tags.peek().firstChild = temp; 
			} else {
				TagNode ptr = tags.peek().firstChild;
				while(ptr.sibling != null) {
					ptr = ptr.sibling;
				}
				ptr.sibling = temp;
			}
			if(isTag) tags.push(temp);
		}
	}
	
	
	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 * 
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {
		replacetag (root, oldTag, newTag);
	}
	private void replacetag(TagNode root, String oldTag, String newTag){
		boolean possible = false;
		if((oldTag.equals("em") && newTag.equals("b"))||(oldTag.equals("b") && newTag.equals("em"))){
			possible = true;
		}else if((oldTag.equals("ol") && newTag.equals("ul"))||(oldTag.equals("ul") && newTag.equals("ol"))){
			possible = true;
		}else if((oldTag.equals("em") && newTag.equals("p"))||(oldTag.equals("p") && newTag.equals("em"))){
			possible = true;
		}else if((oldTag.equals("b") && newTag.equals("p"))||(oldTag.equals("p") && newTag.equals("b"))){
			possible = true;
		}
		
		if (possible == true){
			if (root == null){
				return;
			}
			if (root.tag.equals(oldTag) && root.firstChild != null){
				root.tag = newTag;
			}
			replacetag(root.sibling, oldTag, newTag);
			replacetag(root.firstChild, oldTag, newTag);

		}
		return;
	}
	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 * 
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {
		TagNode table = findTable(root);
		TagNode ptrrow = table.firstChild;
		
		for (int r=1; r!=row; r++){
			ptrrow = ptrrow.sibling;
		}
		
		for (TagNode ptrcol = ptrrow.firstChild; ptrcol != null; ptrcol = ptrcol.sibling){
			TagNode b = new TagNode ("b", ptrcol.firstChild, null);
			ptrcol.firstChild =b;
		}
		
	}
	
	public TagNode findTable(TagNode root){
		if (root == null){
			return null;
		}
		if(root.tag.equals("table")){
			return root;
		}
		TagNode s = findTable (root.sibling);
		TagNode f = findTable (root.firstChild);
		if(s != null){
			return s;
		}
		if(f != null){
			return f;
		}
		return null;
	}
	
	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
	 * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and, 
	 * in addition, all the li tags immediately under the removed tag are converted to p tags. 
	 * 
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag) {
		if(tag.equals("p") || tag.equals("em") || tag.equals("b")) {
			removetag1(root, tag);
		
		}else if(tag.equals("ol") || tag.equals("ul")) {
			removetag2(root, tag);
		}
	}
	
	private void removetag1(TagNode root, String tag){
		if(root == null){
			return;
		}
		if(root.tag.equals(tag) && root.firstChild != null) {
			root.tag = root.firstChild.tag;
			if(root.firstChild.sibling != null) {
				TagNode ptr = null;
				for(ptr = root.firstChild; ptr.sibling != null; ptr = ptr.sibling);
				ptr.sibling = root.sibling;
				root.sibling = root.firstChild.sibling;
			}
			root.firstChild = root.firstChild.firstChild;
		}
		
		removetag1(root.firstChild, tag); 
		removetag1(root.sibling, tag);

	}

	private void removetag2(TagNode root, String tag){
		if(root == null){
			return;
		}
		if(root.tag.equals(tag) && root.firstChild != null) {
			root.tag = "p";
			TagNode ptr = null;
			for(ptr = root.firstChild; ptr.sibling != null; ptr = ptr.sibling){
				ptr.tag = "p"; 
			}
			ptr.tag = "p";
			ptr.sibling = root.sibling;
			root.sibling = root.firstChild.sibling;
			root.firstChild = root.firstChild.firstChild;
		}
		
		removetag2(root.firstChild, tag); 
		removetag2(root.sibling, tag);

	}
	
	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word Word around which tag is to be added
	 * @param tag Tag to be added
	 */
public void addTag(String word, String tag) {
		
		if(tag.equals("em") || tag.equals("b")) 
			addtag(root, word.toLowerCase(), tag); 
	}
	
	private void addtag(TagNode root, String word, String tag){
		if(root == null){
			return; 
		}
		
		addtag(root.firstChild, word, tag);
		addtag(root.sibling, word, tag);
		
		if(root.firstChild == null){
			while(root.tag.toLowerCase().contains(word)){
				String[] str = root.tag.split(" ");
				Boolean getit = false;
				String str2 = "";
				StringBuilder sb = new StringBuilder(root.tag.length());
				int j =0;
				
				for(j = 0; j < str.length; j++){
					if(str[j].toLowerCase().matches(word+"[.?!,-]?")) {

						getit = true;
						str2 = str[j];
						for(int i = j+1; i < str.length; i++) sb.append(str[i]+" ");
						break;
					}
				}
				
				if(!getit){ 
					return;
				}
				
				String remaintag = sb.toString().trim(); 
				
				if(j == 0){ 
					root.firstChild = new TagNode(str2, null, null);
					root.tag = tag;
					
					if(!remaintag.equals("")){ 
						root.sibling = new TagNode(remaintag, null, root.sibling);
						root = root.sibling;
					}	
				} else { 
		
					TagNode textWithTagNode = new TagNode(str2, null, null);
					TagNode newTag = new TagNode(tag, textWithTagNode, root.sibling);
					root.sibling = newTag;
					root.tag = root.tag.replaceFirst(" " + str2, "");
					
						if(!remaintag.equals("")){ 
							root.tag = root.tag.replace(remaintag, "");
							newTag.sibling = new TagNode(remaintag, null, newTag.sibling);
							root = newTag.sibling;
						}
				}
			}
		}

	}

	
	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the
	 * input file from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines. 
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}
	
	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");	
			}
		}
	}
	
}
