describe('appacc', function() {
  var appacc;
  beforeEach(module('appAccelerator'));
  beforeEach(inject(function($injector){
    appacc = $injector.get('appacc');
  }));

  describe('createDownloadUrl', function() {
    it('adds gradle to the URL if gradle is the selected build technology', function() {
      appacc.updateBuildType(appacc.buildType.GRADLE);
      appacc.addSelectedTechnology('testTechnology');
      var url = appacc.createDownloadUrl();
      expect(url).toContain('&build=GRADLE');
    });
    it('adds the artifactId to the URL if it is provided', function() {
      appacc.updateArtifactId('testArtifactId');
      appacc.addSelectedTechnology('testTechnology');
      var url = appacc.createDownloadUrl();
      expect(url).toContain('&artifactId=testArtifactId');
    });
    it('adds the groupId to the URL if it is provided', function() {
        appacc.updateGroupId('test.group.id');
        appacc.addSelectedTechnology('testTechnology');
        var url = appacc.createDownloadUrl();
        expect(url).toContain('&groupId=test.group.id');
    });
  });

  describe('updateBuildType', function() {
    it('sets the build type if a valid value is supplied', function() {
      buildType = appacc.updateBuildType(appacc.buildType.GRADLE);
      expect(buildType).toBe(appacc.buildType.GRADLE);
    });
    it('ignores an invalid value', function() {
      var invalidValue = 'invalid';
      buildType = appacc.updateBuildType(invalidValue);
      expect(buildType).not.toBe(invalidValue);
    });
    it('ignores an undefined value', function() {
      buildType = appacc.updateBuildType(undefined);
      expect(buildType).not.toBe(undefined);
    });
  });

  describe('addSelectedTechnology', function() {
    it('makes a technology select', function() {
      var techId = "wibble";
      appacc.addSelectedTechnology(techId);
      expect(appacc.isSelected(techId)).toBe(true);
    });

    it('only adds a technology once', function() {
      var techId = "wibble";
      appacc.addSelectedTechnology(techId);
      expect(appacc.getSelectedCount()).toBe(1);
    });
  });

  describe('removeSelectedTechnology', function() {
    it('allows you to remove a selected technology', function() {
      var techId = "wibble";
      appacc.addSelectedTechnology(techId);
      appacc.removeSelectedTechnology(techId);
      expect(appacc.isSelected(techId)).toBe(false);
    });
  });
});