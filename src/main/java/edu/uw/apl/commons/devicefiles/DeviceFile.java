/**
 * Copyright © 2015, University of Washington
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 *     * Neither the name of the University of Washington nor the names
 *       of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written
 *       permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL UNIVERSITY OF
 * WASHINGTON BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.uw.apl.commons.devicefiles;

import java.io.File;
import java.io.IOException;

import edu.uw.apl.nativelibloader.NativeLoader;

/**
 * @author Stuart Maclean
 *
 * Capture device files, e.g. /dev/sda, in a Java class.  Device file
 * here nearly always means 'whole disks', like /dev/sda or 'C:'.

 * We use native methods to derive DeviceFile ids (vendor info, serial
 * number etc) and disk sizes (since File.length() returns 0 on a
 * device file).  There is no 'regular' Java API for obtaining a
 * disk's vendor/serial details, which is very platform-specific.
 *
 * We use NativeLibLoader to manage loading any .so/.dylib/.dll
 */
public class DeviceFile {

	public DeviceFile( File f ) throws IOException {
		if( f == null )
			throw new IllegalArgumentException( "Null file!" );

		/*
		  Fail early with IOException if f unreadable...
		*/
		if( !f.canRead() )
			throw new IOException( "Unreadable: " + f.getPath() );

		// Java reports 0 for the size of a device file...
		long len = f.length();
		if( len != 0 )
			throw new IllegalArgumentException( f + ": Unexpected length "
												+ len );
		disk = f;
	}

	/**
	 * @return the File as passed to us in the constructor
	 */
	public File getSource() {
		return disk;
	}

	/**
	 * @return The size of the device file, in bytes. Uses native code
	 * to derive the answer.  The following, Java-only code, will
	 * <b>not</b> work (it will give 0):
	 *
	 * long sz = new File( "/dev/sda" ).length();
	 */
	public long size() {
		return size( disk.getPath() );
	}

	/**
	 * @return a string, concatenated from various platform-specific strings
	 * like
	 * 
	 * vendor, e.g. Seagate, Western Digital etc
	 *
	 * productID, e.g. a model number as named by the vendor
	 *
	 * serial number, hopefully unique across all disks
	 *
	 * The actual concatenation process is arbitrary, currently we are
	 * using '-' chars as a delimiter, and replacing any whitespace
	 * chars found with '_'.
	 */
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

	public String vendorID() {
		return vendorID( disk.getPath() );
	}
	
	public String productID() {
		return productID( disk.getPath() );
	}
	
	public String serialNumber() {
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
