package edu.uw.apl.commons.devicefiles;

import java.io.File;

import org.junit.Test;

public class DeviceFileTest {


	@Test
	public void test1() throws Exception {
		File f = new File( "/dev/sda" );
		if( !f.canRead() )
			return;
		DeviceFile df = new DeviceFile( f );
		System.out.println( "Size: " + df.size() );
		System.out.println( "Size: " + df.getID() );
	}
}

// eof
