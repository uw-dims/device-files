package edu.uw.apl.commons.devicefiles;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import static org.junit.Assert.*;

public class DeviceFileTest {


	@Test
	public void testSDA() throws Exception {
		File f = new File( "/dev/sda" );
		if( !f.canRead() )
			return;
		DeviceFile df = new DeviceFile( f );
		System.out.println( "Size: " + df.size() );
		System.out.println( "Size: " + df.getID() );
	}
	
	@Test
	public void testSDAUnreadable() throws Exception {
		File f = new File( "/dev/sda" );
		if( f.canRead() )
			return;
		try {
			DeviceFile df = new DeviceFile( f );
			fail( "Expected IOException building " + f );
		} catch( IOException ioe ) {
		}
	}
	
	@Test
	public void testJavaSizeIsZero() throws Exception {
		File f = new File( "/dev/sda" );
		assertFalse( f.length() > 0 );
	}
}

// eof
