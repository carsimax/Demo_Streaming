import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:video_player/video_player.dart';
import '../models/video.dart';
import '../services/api_service.dart';

class VideoProvider with ChangeNotifier {
  final ApiService _apiService = ApiService();
  List<Video> _videos = [];
  bool _isLoading = false;
  String? _error;
  VideoPlayerController? _controller;
  bool _isInitialized = false;

  List<Video> get videos => _videos;
  bool get isLoading => _isLoading;
  bool get isInitialized => _isInitialized;
  VideoPlayerController? get controller => _controller;
  String? get error => _error;

  Future<void> fetchVideos() async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      _videos = await _apiService.getVideos();
      _isLoading = false;
      notifyListeners();
    } catch (e) {
      _isLoading = false;
      _error = e.toString();
      notifyListeners();
    }
  }

  String getVideoStreamUrl(String videoId) {
    return _apiService.getVideoStreamUrl(videoId);
  }

  Future<void> initializeVideo(String videoUrl) async {
    try {
      _error = null;
      if (_controller != null) {
        await _controller!.dispose();
      }

      _controller = VideoPlayerController.network(videoUrl);

      await _controller!.initialize();
      _isInitialized = true;
      notifyListeners();
    } catch (e) {
      _error = 'Error al inicializar el video: $e';
      _isInitialized = false;
      notifyListeners();
    }
  }

  @override
  void dispose() {
    _controller?.dispose();
    super.dispose();
  }
}
