package gpio

class ProjectVersion {
  final Integer major
  final Integer minor
  final Integer fix
  final String build
  
  ProjectVersion(Integer major, Integer minor, Integer fix, String build) {
    this.major = major
    this.minor = minor
    this.fix = fix
    this.build = build
  }

  @Override
  String toString() {
    String fullVersion = "$major.$minor.$fix"
    if (build) {
      fullVersion += ".$build"
    }
    fullVersion
  }
}
