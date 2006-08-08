/*
 * This file is subject to the license found in LICENCE.TXT in the root directory of the project.
 * 
 * #SNAPSHOT#
 */
package fr.jayasoft.ivy.ant;

import java.io.File;
import java.net.MalformedURLException;
import java.text.ParseException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import fr.jayasoft.ivy.Ivy;
import fr.jayasoft.ivy.ModuleDescriptor;
import fr.jayasoft.ivy.ModuleRevisionId;
import fr.jayasoft.ivy.filter.FilterHelper;
import fr.jayasoft.ivy.report.ResolveReport;
import fr.jayasoft.ivy.util.Message;

/**
 * This task allow to call the Ivy dependency resolution from ant.
 * 
 * @author Xavier Hanin
 *
 */
public class IvyResolve extends IvyTask {
    private File _file = null;
    private String _conf = null;
    private File _cache = null;
    private String _org = null;
    private String _name = null;
    private String _rev = null;
    private String _revision = null;
    private String _pubdate = null;
    private boolean _haltOnFailure = true;
    private boolean _useCacheOnly = false;
    private String _type = null;
	private boolean _transitive = true;
	private boolean _changing = false;
	private Boolean _keep = null;
    
    public String getDate() {
        return _pubdate;
    }
    public void setDate(String pubdate) {
        _pubdate = pubdate;
    }
    public String getRevision() {
        return _revision;
    }
    public void setRevision(String revision) {
        _revision = revision;
    }
    public File getCache() {
        return _cache;
    }
    public void setCache(File cache) {
        _cache = cache;
    }
    public String getConf() {
        return _conf;
    }
    public void setConf(String conf) {
        _conf = conf;
    }
    public File getFile() {
        return _file;
    }
    public void setFile(File file) {
        _file = file;
    }
    public boolean isHaltonfailure() {
        return _haltOnFailure;
    }
    public void setHaltonfailure(boolean haltOnFailure) {
        _haltOnFailure = haltOnFailure;
    }
    public void setShowprogress(boolean show) {
        Message.setShowProgress(show);
    }
    public boolean isUseCacheOnly() {
        return _useCacheOnly;
    }
    public void setUseCacheOnly(boolean useCacheOnly) {
        _useCacheOnly = useCacheOnly;
    }
    public String getType() {
        return _type;
    }
    public void setType(String type) {
        _type = type;
    }
    
    public void execute() throws BuildException {
        Ivy ivy = getIvyInstance();
        try {
            _conf = getProperty(_conf, ivy, "ivy.configurations");
            _type = getProperty(_type, ivy, "ivy.resolve.default.type.filter");
            if (_cache == null) {
                _cache = ivy.getDefaultCache();
            }
            String[] confs = splitConfs(_conf);
            
            ResolveReport report;
            if (_org  != null) {
            	if (_name == null) {
            		throw new BuildException("'name' is required when using 'org' attribute");
            	}
            	if (_rev == null) {
            		_rev = "latest.integration";
            	}
	            report = ivy.resolve(
	            		ModuleRevisionId.newInstance(_org, _name, _rev),
	                    confs, 
	                    _transitive,
	                    _changing,
	                    _cache, 
	                    getPubDate(_pubdate, null), 
	                    doValidate(ivy),
	                    _useCacheOnly,
	                    FilterHelper.getArtifactTypeFilter(_type));
            	
            } else {
            	if (_name != null) {
            		throw new BuildException("'name' not allowed when not using 'org' attribute");
            	}
	            if (_file == null) {
	                _file = new File(getProject().getBaseDir(), getProperty(ivy, "ivy.dep.file"));
	            }
	            _revision = getProperty(_revision, ivy, "ivy.revision");
	            report = ivy.resolve(
	                    _file.toURL(), 
	                    _revision, 
	                    confs, 
	                    _cache, 
	                    getPubDate(_pubdate, null), 
	                    doValidate(ivy),
	                    _useCacheOnly,
	                    _transitive,
	                    FilterHelper.getArtifactTypeFilter(_type));
            }
            if (isHaltonfailure() && report.hasError()) {
                throw new BuildException("resolve failed - see output for details");
            }
            
            setResolved(report, isKeep());
            
            if (isKeep()) {
            	ModuleDescriptor md = report.getModuleDescriptor();
	            // put resolved infos in ant properties and ivy variables
	            // putting them in ivy variables is important to be able to change from one resolve call to the other
	            getProject().setProperty("ivy.organisation", md.getModuleRevisionId().getOrganisation());
	            ivy.setVariable("ivy.organisation", md.getModuleRevisionId().getOrganisation());
	            getProject().setProperty("ivy.module", md.getModuleRevisionId().getName());
	            ivy.setVariable("ivy.module", md.getModuleRevisionId().getName());
	            getProject().setProperty("ivy.revision", md.getResolvedModuleRevisionId().getRevision());
	            ivy.setVariable("ivy.revision", md.getResolvedModuleRevisionId().getRevision());
	            boolean hasChanged = report.hasChanged();
	            getProject().setProperty("ivy.deps.changed", String.valueOf(hasChanged));
	            ivy.setVariable("ivy.deps.changed", String.valueOf(hasChanged));
	            if (_conf.trim().equals("*")) {
	                getProject().setProperty("ivy.resolved.configurations", mergeConfs(md.getConfigurationsNames()));
	                ivy.setVariable("ivy.resolved.configurations", mergeConfs(md.getConfigurationsNames()));
	            } else {
	                getProject().setProperty("ivy.resolved.configurations", _conf);
	                ivy.setVariable("ivy.resolved.configurations", _conf);
	            }
            }
        } catch (MalformedURLException e) {
            throw new BuildException("unable to convert given ivy file to url: "+_file+": "+e, e);
        } catch (ParseException e) {
            log(e.getMessage(), Project.MSG_ERR);
            throw new BuildException("syntax errors in ivy file: "+e, e);
        } catch (Exception e) {
            throw new BuildException("impossible to resolve dependencies: "+e, e);
        }
    }
	public String getName() {
		return _name;
	}
	public void setName(String module) {
		_name = module;
	}
	public String getOrg() {
		return _org;
	}
	public void setOrg(String organisation) {
		_org = organisation;
	}
	public boolean isTransitive() {
		return _transitive;
	}
	public void setTransitive(boolean transitive) {
		_transitive = transitive;
	}
	public boolean isChanging() {
		return _changing;
	}
	public void setChanging(boolean changing) {
		_changing = changing;
	}
	public String getRev() {
		return _rev;
	}
	public void setRev(String rev) {
		_rev = rev;
	}
	public boolean isKeep() {
		return _keep == null ? _org == null : _keep.booleanValue();
	}
	public void setKeep(boolean keep) {
		_keep = Boolean.valueOf(keep);
	}
}
