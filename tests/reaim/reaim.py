# Needs autoconf & automake & libtool to be installed. Ewwwwwwwwwwwwwwwwwwwwww
import test
from autotest_utils import *

class reaim(test.test):
	version = 1

	# http://prdownloads.sourceforge.net/re-aim-7/osdl-aim-7.0.1.13.tar.gz
	def setup(self, tarball = 'osdl-aim-7.0.1.13.tar.gz'):
		tarball = unmap_url(self.bindir, tarball, self.tmpdir)
		extract_tarball_to_dir(tarball, self.srcdir)

		self.job.setup_dep(['libaio'])
		libs = '-L' + self.autodir + '/deps/libaio/lib -laio'
		cflags = '-I ' + self.autodir + '/deps/libaio/include'
		var_libs = 'LIBS="' + libs + '"'
		var_cflags  = 'CFLAGS="' + cflags + '"'
		self.make_flags = var_libs + ' ' + var_cflags

		os.chdir(self.srcdir)
		system('./bootstrap')
		system('./configure')
		system('patch -p1 < ../reaim.diff')
		system(self.make_flags + ' make')
		os.rename('src/reaim', 'reaim')


	def initialize(self):
		self.ldlib = 'LD_LIBRARY_PATH=%s/deps/libaio/lib'%(self.autodir)


	def execute(self, iterations = 1, workfile = 'workfile.short', 
			start = 1, end = 10, increment = 2,
			extra_args = '', tmpdir = None):
		if not tmpdir:
			tmpdir = self.tmpdir
		
		# -f workfile
		# -s <number of users to start with>
		# -e <number of users to end with>
		# -i <number of users to increment>
		workfile = os.path.join('data', workfile)
		args = "-f %s -s %d -e %d -i %d" %(workfile,start,end,increment)
		config = os.path.join(self.srcdir, 'reaim.config')
		system('cp -f %s/reaim.config %s' % (self.bindir, config))
		args += ' -c ./reaim.config'
		open(config, 'a+').write("DISKDIR %s\n" % (tmpdir))
		os.chdir(self.srcdir)
		print os.getcwd()
		cmd = self.ldlib + ' ./reaim ' + args + ' ' + extra_args

		for i in range(1, iterations+1):
			system(cmd)

		# Do a profiling run if necessary
		profilers = self.job.profilers
		if profilers.present():
			profilers.start(self)
			system(cmd)
			profilers.stop(self)
			profilers.report(self)
