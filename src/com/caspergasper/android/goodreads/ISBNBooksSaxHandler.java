package com.caspergasper.android.goodreads;

import static com.caspergasper.android.goodreads.GoodReadsApp.TAG;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;


class ISBNBooksSaxHandler extends DefaultHandler {
    private StringBuilder builder;
    private final static String NAME = "name";
    private final static String TITLE = "title";
    private final static String DESCRIPTION = "description";
    private final static String AVERAGE_RATING = "average_rating";
    private final static String LINK = "link";
    private final static String SMALL_IMAGE_URL = "small_image_url";
    private final static String AUTHORS = "authors";
    private final static String REVIEWS = "reviews";
    private final static String MY_REVIEW = "my_review";
    private final static String SHELF = "shelf";
    private final static String ID = "id";
    
    private UserData userdata;
    private boolean inAuthors = false;
    private boolean inReviews = false;
    private boolean inMyReview = false;
    private static final int url_length = BooksActivity.GOODREADS_IMG_URL.length(); 
    private int lastBookPos = 0;
    
    ISBNBooksSaxHandler(UserData ud) {
    	userdata = ud;
    	builder = new StringBuilder();
    }
    
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        super.characters(ch, start, length);
        builder.append(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String name)
            throws SAXException {
    	super.endElement(uri, localName, name);	
    	if(localName.equalsIgnoreCase(ID)) {
    		if(userdata.tempBooks.size() == 0) {
    			userdata.tempBooks.add(new Book(Integer.parseInt(builder.toString().trim())));
    		}
    	} else if(localName.equalsIgnoreCase(TITLE)) {
        	userdata.tempBooks.get(lastBookPos).title = builder.toString().trim();
        } else if(localName.equalsIgnoreCase(DESCRIPTION)) {
    		userdata.tempBooks.get(lastBookPos).description 
    		= builder.toString().trim().replaceAll("&lt;/?div&gt;", "");
        } else if(localName.equalsIgnoreCase(LINK)) { 
        	if(userdata.tempBooks.get(lastBookPos).bookLink == null) {
        		userdata.tempBooks.get(lastBookPos).setBookLink(builder.toString().trim());
        	}
        } else if(localName.equalsIgnoreCase(NAME)) {
        	if(inAuthors) {
        		userdata.tempBooks.get(lastBookPos).author += 
        		builder.toString().trim() + " ";
        	}
        } else if(localName.equalsIgnoreCase(AVERAGE_RATING)) {
        	userdata.tempBooks.get(lastBookPos).average_rating = 
        		builder.toString().trim();
        } else if(localName.equalsIgnoreCase(SMALL_IMAGE_URL)) {
    		String url = builder.toString().trim();
    		if(!inReviews && !inAuthors && 
    				url.substring(0, url_length).compareTo(BooksActivity.GOODREADS_IMG_URL) == 0  
    			&&	userdata.tempBooks.get(lastBookPos).small_image_url == null) {
    			userdata.tempBooks.get(lastBookPos).small_image_url = 
    				url.substring(url_length);
        	}
        } else if(localName.equalsIgnoreCase(AUTHORS)) {
        	inAuthors = false;
        } else if(localName.equalsIgnoreCase(MY_REVIEW)) {
        	inMyReview = false;
        } else {
//            	Log.d(TAG, "tag: " + localName);
//            	Log.d(TAG, "value: " + builder.toString().trim());
        }
        builder.setLength(0);
    }

    @Override
    public void startElement(String uri, String localName, String name,
            Attributes attributes) throws SAXException {
    	super.startElement(uri, localName, name, attributes);
    	if(localName.equalsIgnoreCase(AUTHORS)){
        	inAuthors = true;
        } else if(localName.equalsIgnoreCase(REVIEWS)){
        	inReviews = true;
        }  else if(localName.equalsIgnoreCase(MY_REVIEW)){
        	inMyReview = true;
        } else if(localName.equalsIgnoreCase(SHELF)){
        	if(inMyReview) {
        		userdata.tempBooks.get(lastBookPos).shelves.add(attributes.getValue(NAME));
        	}
        }
    }
    
//    @Override
//    public void startDocument() throws SAXException {
//    	super.startDocument();
//    	
//    }

}