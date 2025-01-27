# FIXME: the LIC_FILES_CHKSUM values have been updated by 'devtool upgrade'.
# The following is the difference between the old and the new license text.
# Please update the LICENSE value if needed, and summarize the changes in
# the commit message via 'License-Update:' tag.
# (example: 'License-Update: copyright years updated.')
#
# The changes:
#
# --- LICENSE
# +++ LICENSE
# @@ -206,8 +206,8 @@
#     written permission.
#  
#  
# -The code in crypto/kyber/pqcrystals-kyber_kyber512_ref carries the
# -Public Domain license:
# +The code in crypto/kyber/pqcrystals-kyber_kyber512_ref and
# +crypto/ml_kem/ml_kem_ipd_ref_common carries the Public Domain license:
#  
#  Public Domain (https://creativecommons.org/share-your-work/public-domain/cc0/)
#  
# 
#

SUMMARY = "AWS libcrypto (AWS-LC)"
DESCRIPTION = "AWS-LC is a general-purpose cryptographic library maintained by the AWS Cryptography team for AWS and their customers. It іs based on code from the Google BoringSSL project and the OpenSSL project."

HOMEPAGE = "https://github.com/awslabs/aws-lc"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d7bea8c886a6934b7d38eb42bee9019c"

PROVIDES += "aws/lc"

BRANCH ?= "main"

SRC_URI = "\
    git://github.com/awslabs/aws-lc.git;protocol=https;branch=${BRANCH} \
    file://run-ptest \
    "
SRCREV = "4e54dd8363396f257d7a2317c48101e18170e6fb"
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>.*)"

S = "${WORKDIR}/git"

inherit cmake ptest pkgconfig

PACKAGECONFIG ??= "\
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'with-tests', '', d)} \
    "

# CMAKE_CROSSCOMPILING=ON will disable building the tests
PACKAGECONFIG[with-tests] = "-DBUILD_TESTING=ON -DCMAKE_CROSSCOMPILING=OFF,-DBUILD_TESTING=OFF,"

# enable PACKAGECONFIG = "static" to build static instead of shared libs
PACKAGECONFIG[static] = "-DBUILD_SHARED_LIBS=OFF,-DBUILD_SHARED_LIBS=ON"

do_install_ptest () {
   install -d ${D}${PTEST_PATH}/tests
   cp -r ${B}/libboringssl_gtest.so ${D}${PTEST_PATH}/tests/
   install -m 0755 ${B}/ssl/ssl_test ${D}${PTEST_PATH}/tests/
}

EXTRA_OECMAKE += "\
    -DDISABLE_PERL=ON \
    -DDISABLE_GO=ON \
"

CXXFLAGS += "-Wno-ignored-attributes"

FILES:${PN} += "\
    ${libdir}/libcrypto.so \
    ${libdir}/libssl.so \
    ${libdir}/libdecrepit.so \
    "

FILES:${PN}-dev += "${libdir}/*/cmake"

# also test depend-on-us packages build
RDEPENDS:${PN}-ptest = "\
    aws-c-auth \
    aws-c-cal \
    aws-c-compression \
    aws-c-io \
    aws-c-s3 \
    aws-checksums \
    "

# Notify that libraries are not versioned
FILES_SOLIBSDEV = ""

BBCLASSEXTEND = "native nativesdk"
