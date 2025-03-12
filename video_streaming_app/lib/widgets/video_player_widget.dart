import 'package:flutter/material.dart';
import 'package:video_player/video_player.dart';
import 'package:chewie/chewie.dart';

class VideoPlayerWidget extends StatefulWidget {
  final String videoUrl;
  final bool isActive;

  const VideoPlayerWidget({
    Key? key,
    required this.videoUrl,
    required this.isActive,
  }) : super(key: key);

  @override
  _VideoPlayerWidgetState createState() => _VideoPlayerWidgetState();
}

class _VideoPlayerWidgetState extends State<VideoPlayerWidget> {
  late VideoPlayerController _videoPlayerController;
  ChewieController? _chewieController;
  bool _isInitialized = false;
  bool _hasError = false;
  String _errorMessage = '';

  @override
  void initState() {
    super.initState();
    _initializePlayer();
  }

  @override
  void didUpdateWidget(VideoPlayerWidget oldWidget) {
    super.didUpdateWidget(oldWidget);

    // Pausar o reproducir según visibilidad
    if (widget.isActive != oldWidget.isActive) {
      if (widget.isActive) {
        _videoPlayerController.play();
      } else {
        _videoPlayerController.pause();
      }
    }

    // Reiniciar el video si cambia la URL
    if (widget.videoUrl != oldWidget.videoUrl) {
      _disposeControllers();
      _initializePlayer();
    }
  }

  Future<void> _initializePlayer() async {
    _videoPlayerController = VideoPlayerController.networkUrl(
      Uri.parse(widget.videoUrl),
    );

    try {
      await _videoPlayerController.initialize();

      // Configuración para reproducción en bucle
      _chewieController = ChewieController(
        videoPlayerController: _videoPlayerController,
        autoPlay: widget.isActive,
        looping: true,
        // Ocultar controles para experiencia tipo TikTok
        showControls: false,
        aspectRatio: _videoPlayerController.value.aspectRatio,
        errorBuilder: (context, errorMessage) {
          return Center(
            child: Text(errorMessage, style: TextStyle(color: Colors.white)),
          );
        },
      );

      setState(() {
        _isInitialized = true;
      });
    } catch (e) {
      setState(() {
        _hasError = true;
        _errorMessage = 'Error al cargar el video: ${e.toString()}';
      });
      print("Error initializing video player: $e");
    }
  }

  void _disposeControllers() {
    _videoPlayerController.dispose();
    _chewieController?.dispose();
  }

  @override
  void dispose() {
    _disposeControllers();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    if (_hasError) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.error_outline, size: 48, color: Colors.red),
            SizedBox(height: 16),
            Text(
              _errorMessage,
              style: TextStyle(color: Colors.white),
              textAlign: TextAlign.center,
            ),
          ],
        ),
      );
    }

    if (!_isInitialized) {
      return Center(
        child: CircularProgressIndicator(
          valueColor: AlwaysStoppedAnimation<Color>(Colors.white),
        ),
      );
    }

    return GestureDetector(
      onTap: () {
        // Pausar/reproducir al tocar la pantalla
        if (_videoPlayerController.value.isPlaying) {
          _videoPlayerController.pause();
        } else {
          _videoPlayerController.play();
        }
      },
      child: Container(
        color: Colors.black,
        child: Chewie(controller: _chewieController!),
      ),
    );
  }
}
