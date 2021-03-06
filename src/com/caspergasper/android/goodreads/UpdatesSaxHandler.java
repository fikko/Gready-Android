package com.caspergasper.android.goodreads;

import static com.caspergasper.android.goodreads.GoodReadsApp.TAG;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

class UpdatesSaxHandler extends DefaultHandler {
    private StringBuilder builder;
    private final static String NAME = "name";
    private final static String ACTION_TEXT = "action_text";
    private final static String BODY = "body";
    private final static String ID = "id";
    private final static String LINK = "link";
    private final static String UPDATE = "update";
    private final static String TYPE = "type";
    private final static String REVIEW = "review";
    private final static String COMMENT = "comment";
    private final static String IMAGE_URL = "image_url";
    private final static String ACTOR = "actor";

    private UserData userdata;
    private boolean inReview = false;
    private boolean inActor = false;
    
    UpdatesSaxHandler(UserData ud) {
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
    	int pos = userdata.tempUpdates.size() - 1;
        if(localName.equalsIgnoreCase(ACTION_TEXT)) {
        	userdata.tempUpdates.add(new Update(builder.toString().trim().replaceAll("</?a[^>]+>", "")));
        } else if(localName.equalsIgnoreCase(NAME)) {
        	if(inActor) {
        		userdata.tempUpdates.get(pos).username = builder.toString().trim();
        	}
        }  else if(localName.equalsIgnoreCase(BODY)) {
        	userdata.tempUpdates.get(pos).body = builder.toString().trim();        	
        }  else if(localName.equalsIgnoreCase(ID)) {
        	if(inActor) {
        		userdata.tempUpdates.get(pos).id = Integer.parseInt(builder.toString().trim());
        	}
        }  else if(localName.equalsIgnoreCase(LINK)) {
        	if(inReview) {
        		String url = builder.toString().trim();
        		userdata.tempUpdates.get(pos).updateLink = url.substring(url.lastIndexOf('/'));
        		inReview = false;
        	}
        }  else if(localName.equalsIgnoreCase(IMAGE_URL)) {
        	if(inActor) {
        		String url = builder.toString().trim();
        		if(url.substring(0, GoodReadsApp.GOODREADS_IMG_URL_LENGTH).compareTo(
        				GoodReadsApp.GOODREADS_IMG_URL) == 0) {
        			userdata.tempUpdates.get(pos).imgUrl = url.substring(GoodReadsApp.GOODREADS_IMG_URL_LENGTH);
        		}
        		inActor = false;
        	}
        } else {

        }

//    	Log.d(TAG, "value: " + builder.toString().trim());
//    	Log.d(TAG, "tag: </" + localName + ">");
    	builder.setLength(0);
    }
    
    @Override
    public void startElement(String uri, String localName, String name,
            Attributes attributes) throws SAXException {
    	super.startElement(uri, localName, name, attributes);
//    	Log.d(TAG, "tag: <" + localName + ">");
    	if(localName.equalsIgnoreCase(UPDATE)) {
    		if(attributes.getValue(TYPE).equalsIgnoreCase(REVIEW) ||
    				attributes.getValue(TYPE).equalsIgnoreCase(COMMENT)) {
    			inReview = true;
    		} 
//    		Log.d(TAG, "attribval:" + attributes.getValue(TYPE));
    	} else if(localName.equalsIgnoreCase(ACTOR)) {
    		inActor = true;
    	}
//    	for(int i = 0; i<attributes.getLength(); i++) {
//    		Log.d(TAG, "attrib:" + attributes.getLocalName(i));
//    		Log.d(TAG, "attribval:" + attributes.getValue(i));
//    	}
    	
    }

}
