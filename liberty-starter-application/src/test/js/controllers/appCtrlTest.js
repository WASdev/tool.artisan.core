describe('appCtrl', function() {
  beforeEach(module('appAccelerator'));

  var $controller;

  beforeEach(inject(function(_$controller_){
    $controller = _$controller_;
  }));

  describe('$scope.buildType', function() {
    it('contains maven and gradle as options', function() {
      var $scope = {};
      var controller = $controller('appCtrl', { $scope: $scope });
      expect($scope.buildType.GRADLE).toBe('GRADLE');
      expect($scope.buildType.MAVEN).toBe('MAVEN');
    });
    describe('$scope.deploy.buildType', function() {
      it('defaults to maven', function() {
        var $scope = {};
        var controller = $controller('appCtrl', { $scope: $scope });
        expect($scope.deploy.buildType).toBe('MAVEN');
      });
    });
  });
});