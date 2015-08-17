package edu.uw.apl.commons.devicefiles;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;

import edu.uw.apl.nativelibloader.NativeLoader;

/**
 * @author Stuart Maclean
 *
 * Capture device files, e.g. /dev/sda, in a Java class.  We use
 * native methods to derive DeviceFile ids (vendor info, serial number
 * etc) and disk sizes (since File.length() returns 0 on a device
 * file).  There is no 'regular' Java API for obtaining a disk's
 * vendor/serial details, which is very platform-specific.
 *
 * We use NativeLibLoader to manage loading any .so/.dylib/.dll
 */
public class DeviceFile {

	public DeviceFile( File f ) throws IOException {
		if( f == null )
			throw new IllegalArgumentException( "Null file!" );

		/*
		  Fail early with IOException if no such file, before jni...
		*/
		FileInputStream fis = new FileInputStream( f );
		fis.close();

		// Java reports 0 for the size of a device file...
		long len = f.length();
		if( len != 0 )
			throw new IllegalArgumentException( f + ": Unexpected length "
												+ len );
		disk = f;
	}

	public File getSource() {
		return disk;
	}
	
	public long size() {
		return size( disk.getPath() );
	}

	public String getID() {
		String v = vendorID( disk.getPath() );
		if( v != null )
			v = v.trim();
		String p = productID( disk.getPath() );
		if( p != null )
			p = p.trim();
		String s = serialNumber( disk.getPath() );
		if( s != null )
			s = s.trim();
		String concat = v + "-" + p + "-" + s;
		return concat.replaceAll( "\\s", "_" );
	}

	// for debug purposes
	String vendorID() {
		return vendorID( disk.getPath() );
	}
	String productID() {
		return productID( disk.getPath() );
	}
	String serialNumber() {
		return serialNumber( disk.getPath() );
	}

	private native long size( String path );

	private native String vendorID( String path );
	private native String productID( String path );
	private native String serialNumber( String path );
	
	final File disk;

	static private final String libName = "device-files";
    static {
		try {
			NativeLoader.load( DeviceFile.class, libName );
		} catch( Throwable t ) {
			throw new ExceptionInInitializerError( t );
		}
    }
}

// eof
