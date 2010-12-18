package org.sapid.checker.eclipse.codeassist.dtd;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;

public class Visitor implements IResourceVisitor{
	String dtdpath;

	@Override
	public boolean visit(IResource resource) throws CoreException {
		  String rpath = resource.getLocation().toString();

		  if(rpath.matches(".*[.]dtd") == true){
			  dtdpath = rpath;
		  }
		return true;
	}

	public String getDTDPath(){
		return dtdpath;
	}

}
